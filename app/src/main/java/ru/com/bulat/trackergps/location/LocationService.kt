package ru.com.bulat.trackergps.location

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import org.osmdroid.util.GeoPoint
import ru.com.bulat.trackergps.MainActivity
import ru.com.bulat.trackergps.R

class LocationService : Service() {

    private var lastLocation: Location? = null
    private var distance = 0.0f
    private lateinit var geoPointsList: ArrayList<GeoPoint>

    private lateinit var locProvider: FusedLocationProviderClient
    private lateinit var locRequest: LocationRequest

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startNotification()
        startLocationUpdate()
        isRunning = true
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        geoPointsList = ArrayList()
        initLocation()
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        locProvider.removeLocationUpdates(locCallBack)
    }


    private fun startNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nChanel = NotificationChannel(
                CHANEL_ID,
                "Location Service",
                NotificationManager.IMPORTANCE_NONE,

                )
            val nManager = getSystemService(NotificationManager::class.java) as NotificationManager
            nManager.createNotificationChannel(nChanel)
        }
        val nIntent = Intent(this, MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(
            this,
            10,
            nIntent,
            PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(
            this,
            CHANEL_ID
        )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.tracker_running))
            .setContentIntent(pIntent)
            .build()
        startForeground(99, notification)
    }

    private val locCallBack = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val currentLocation = locationResult.lastLocation
            if (lastLocation != null && currentLocation != null) {
                //if (currentLocation.speed > 0.2) {
                distance += currentLocation.distanceTo(lastLocation!!)
                //}
                geoPointsList.add(GeoPoint(currentLocation.latitude, currentLocation.longitude))
                val locationModel = LocationModel(
                    currentLocation.speed,
                    distance,
                    geoPointsList
                )

                sendLocationData(locationModel)
            }
            lastLocation = currentLocation
        }
    }

    private fun sendLocationData(locationModel: LocationModel) {
        val intent = Intent (LOCATION_MODEL_INTENT)
        intent.putExtra(LOCATION_MODEL_INTENT, locationModel)
        LocalBroadcastManager
            .getInstance(applicationContext)
            .sendBroadcast(intent)
    }

    private fun initLocation() {
        /*locRequest =  LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 5000
            priority = PRIORITY_HIGH_ACCURACY
        }*/


        locRequest = LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, 1000).apply {
            setGranularity(Granularity.GRANULARITY_FINE)
            setWaitForAccurateLocation(true)
        }.build()


        locProvider = LocationServices.getFusedLocationProviderClient(baseContext)
    }

    private fun startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locProvider.requestLocationUpdates(
            locRequest,
            locCallBack,
            Looper.myLooper()
        )
    }

    companion object {
        const val LOCATION_MODEL_INTENT = "location_intent"
        const val CHANEL_ID = "chanel_1"
        var isRunning = false
        var startTime = 0L
    }

}