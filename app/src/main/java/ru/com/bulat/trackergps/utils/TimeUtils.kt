package ru.com.bulat.trackergps.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

object TimeUtils {

    @SuppressLint("SimpleDateFormat")
    private val timeFormatter = SimpleDateFormat("HH:mm:ss")
    private val dateFormatter = SimpleDateFormat("dd.MM.YYYY HH:mm")
    fun getTime (timeInMillis : Long) : String {
        val cv = Calendar.getInstance()
        cv.timeInMillis = timeInMillis
        timeFormatter.timeZone = TimeZone.getTimeZone("UTC")
        return timeFormatter.format(cv.time)
    }

    fun getDate () :String {
        val cv = Calendar.getInstance()
        return dateFormatter.format(cv.time)
    }
}