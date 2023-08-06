package ru.com.bulat.trackergps.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import ru.com.bulat.trackergps.databinding.FragmentMainBinding
import ru.com.bulat.trackergps.utils.DialogManager
import ru.com.bulat.trackergps.utils.showToast


class MainFragment : Fragment() {

    private lateinit var pLancherLocation: ActivityResultLauncher<Array<String>>
    private lateinit var pLancherBackGround: ActivityResultLauncher<Array<String>>

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

    }

    override fun onResume() {
        super.onResume()
        checkLocationPermission()
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

    private fun  checkLocationEnabled() {

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

    companion object {

        @JvmStatic
        fun newInstance() = MainFragment()
    }
}