package ru.com.bulat.trackergps.location

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import ru.com.bulat.trackergps.MainActivity
import ru.com.bulat.trackergps.R

class LocationService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("AAA","LocalService onStartCommand: isRunning = $isRunning; startTime = ${startTime}")
        startNotification()
        isRunning = true
        return START_STICKY
    }

    override fun onCreate() {
        Log.d("AAA","LocalService onCreate isRunning = $isRunning; startTime = $startTime")
        super.onCreate()

    }

    override fun onDestroy() {
        Log.d("AAA","LocalService onDestroy isRunning = $isRunning; isRunning = $startTime")
        super.onDestroy()
        isRunning = false
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

    companion object {
        const val CHANEL_ID = "chanel_1"
        var isRunning = false
        var startTime = 0L
    }

}