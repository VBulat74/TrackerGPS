package ru.com.bulat.trackergps.fragments

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.Distance
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import ru.com.bulat.trackergps.MaimViewModel
import ru.com.bulat.trackergps.R
import ru.com.bulat.trackergps.databinding.FragmentMainBinding
import ru.com.bulat.trackergps.location.LocationModel
import ru.com.bulat.trackergps.location.LocationService
import ru.com.bulat.trackergps.utils.DialogManager
import ru.com.bulat.trackergps.utils.TimeUtils
import ru.com.bulat.trackergps.utils.showToast
import java.util.Timer
import java.util.TimerTask

class MainFragment : Fragment() {

    private var isServiceRunning: Boolean = false
    private var timer: Timer? = null
    private var startTime = 0L

    private lateinit var pLancherLocation: ActivityResultLauncher<Array<String>>
    private lateinit var pLancherBackGround: ActivityResultLauncher<Array<String>>

    private val viewModel : MaimViewModel by activityViewModels()

    private lateinit var binding: FragmentMainBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsOSM()
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerPermissions()
        setOnClicks()
        checkServiceState()
        updateTime()
        registerLocationReceiver()
        locationUpdate()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        checkLocationPermission()

    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun setOnClicks() = with(binding) {
        val listener = onClicks()

        fbtnStartStop.setOnClickListener(listener)
        fbtnCenter.setOnClickListener(listener)
    }

    private fun onClicks(): OnClickListener {
        return OnClickListener {
            when (it.id) {
                R.id.fbtnStartStop -> {
                    checkService()
                }

                R.id.fbtnCenter -> {}
            }
        }
    }

    private fun locationUpdate() = with(binding) {
        viewModel.locationUpdate.observe(viewLifecycleOwner) { locationModel ->
            val distance = getString(R.string.distance, String.format("%.1f", locationModel.distance))
            val velocity = getString(R.string.velocity, String.format("%.1f", 3.6f * locationModel.velocity))
            val averageVelocity = getString(R.string.averge_velocity, getAverageVelocity(locationModel.distance))
            tvDistance.text = distance
            tvVelocity.text = velocity
            tvAvrVelocity.text = averageVelocity
        }
    }

    private fun updateTime() {
        viewModel.timeData.observe(viewLifecycleOwner) {
            binding.tvTime.text = it
        }
    }

    private fun getAverageVelocity (distance: Float) : String {
        return String.format("%.1f", 3.6f*(distance / ((System.currentTimeMillis()- startTime)/1000.0f)))
    }

    private fun startTimer() {
        timer?.cancel()
        timer = Timer()

        startTime = LocationService.startTime

        timer?.schedule(
            object : TimerTask() {
                override fun run() {
                    activity?.runOnUiThread {
                        viewModel.timeData.value = "Time: ${getCurrentTime()}"
                    }
                }

            },
            1000,
            1000
        )
    }

    private fun getCurrentTime(): String {
        return TimeUtils.getTime(System.currentTimeMillis() - startTime)
    }

    private fun checkServiceState() {
        isServiceRunning = LocationService.isRunning
        if (isServiceRunning) {
            binding.fbtnStartStop.setImageResource(R.drawable.ic_stop)
            startTimer()
        } else {
            binding.fbtnStartStop.setImageResource(R.drawable.ic_play)
        }
    }

    private fun checkService() {
        if (isServiceRunning) {
            stopLocationService()
        } else {
            startLocationService()
        }
        isServiceRunning = !isServiceRunning
    }

    private fun stopLocationService() {
        activity?.stopService(Intent(activity, LocationService::class.java))
        binding.fbtnStartStop.setImageResource(R.drawable.ic_play)
        timer?.cancel()
    }

    private fun startLocationService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity?.startForegroundService(
                Intent(
                    activity,
                    LocationService::class.java
                )
            )
        } else {
            activity?.startService(Intent(activity, LocationService::class.java))
        }
        binding.fbtnStartStop.setImageResource(R.drawable.ic_stop)
        LocationService.startTime = System.currentTimeMillis()
        startTimer()
    }

    private fun registerPermissions() {
        pLancherLocation =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (it[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                    // User granted location permission
                    // Now check if android version >= 11, if >= 11 check for Background Location Permission
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (ContextCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            initOSM()
                            checkLocationEnabled()
                        } else {
                            // Ask for Background Location Permission
                            askPermissionForBackgroundUsage()
                        }
                    } else {
                        initOSM()
                        checkLocationEnabled()

                    }
                } else {
                    showToast("1 Permissions ACCESS_FINE_LOCATION Denied!")
                }
            }

        pLancherBackGround =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (it[Manifest.permission.ACCESS_BACKGROUND_LOCATION] == true) {
                    initOSM()
                    checkLocationEnabled()
                } else {
                    initOSM()
                    checkLocationEnabled()
                    showToast("2 Permissions ACCESS_BACKGROUND_LOCATION Denied!")
                }
            }

    }

    private fun settingsOSM() {
        Configuration
            .getInstance()
            .load(
                (activity as AppCompatActivity),
                activity?.getSharedPreferences("osm_pref", Context.MODE_PRIVATE),
            )

        Configuration.getInstance().userAgentValue = BuildConfig.LIBRARY_PACKAGE_NAME
    }

    private fun initOSM() = with(binding) {
        map.controller.setZoom(20.0)

        val mLocationProvider = GpsMyLocationProvider(activity)
        val mLocOverlay = MyLocationNewOverlay(mLocationProvider, map)
        mLocOverlay.enableMyLocation()
        mLocOverlay.enableFollowLocation()
        mLocOverlay.runOnFirstFix {
            map.overlays.clear()
            map.overlays.add(mLocOverlay)
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Fine Location permission is granted
            // Check if current android version >= 11, if >= 11 check for Background Location permission
            initOSM()
            checkLocationEnabled()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    initOSM()
                    checkLocationEnabled()
                } else {
                    // Ask for Background Location Permission
                    askPermissionForBackgroundUsage()
                }
            }
        } else {
            // Fine Location Permission is not granted so ask for permission
            askForLocationPermission()
        }
    }

    private fun askForLocationPermission() {

        val arrPermission = if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            AlertDialog.Builder(requireContext())
                .setTitle("Permission Needed!")
                .setMessage("Location Permission Needed!")
                .setPositiveButton(
                    "OK"
                ) { _, _ ->

                    pLancherLocation.launch(arrPermission)

                }
                .setNegativeButton("CANCEL") { _, _ ->
                    // Permission is denied by the user
                    showToast("3 Permission ACCESS_FINE_LOCATION is denied by the user")
                }
                .create().show()
        } else {
            pLancherLocation.launch(arrPermission)

        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun askPermissionForBackgroundUsage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        ) {
            AlertDialog.Builder(requireActivity())
                .setTitle("Permission Needed!")
                .setMessage("Background Location Permission Needed!, tap \"Allow all time in the next screen\"")
                .setPositiveButton(
                    "OK"
                ) { _, _ ->

                    pLancherBackGround.launch(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION))

                }
                .setNegativeButton(
                    "CANCEL"
                ) { _, _ ->
                    // User declined for Background Location Permission.
                    initOSM()
                    showToast(" 4 User declined for Background Location Permission")
                }
                .create().show()
        } else {
            pLancherBackGround.launch(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION))

        }
    }

    private fun checkLocationEnabled() {

        val locManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!isEnabled) {
            DialogManager.showDialogLocationEnabled(
                activity as AppCompatActivity,
                object : DialogManager.Listener {
                    override fun onClick() {
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                }
            )
        } else {
            showToast("Location enabled")
        }


    }

    /*
        private fun registerPermissions() {
            pLancher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (it[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                    initOSM()
                } else {
                    showToast("Permissions Denied!")
                }
            }
        }

        private fun checkLocPermission() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                checkPermissionAfterQ()
            } else {
                checkPermissionBeforeQ()
            }
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        private fun checkPermissionAfterQ() {
            if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                && checkPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            ) {
                initOSM()
            } else {
                pLancher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                )
            }
        }

        private fun checkPermissionBeforeQ() {
            if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            ) {
                initOSM()
            } else {
                pLancher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
            }
        }*/

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == LocationService.LOCATION_MODEL_INTENT) {
                val locationModel = intent.getSerializableExtra(
                    LocationService.LOCATION_MODEL_INTENT,
                ) as LocationModel

                viewModel.locationUpdate.value = locationModel
            }
        }
    }

    private fun registerLocationReceiver() {
        val locationFilter = IntentFilter(LocationService.LOCATION_MODEL_INTENT)
        LocalBroadcastManager
            .getInstance(activity as AppCompatActivity)
            .registerReceiver(receiver, locationFilter)
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}