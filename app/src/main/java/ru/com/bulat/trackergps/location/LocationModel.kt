package ru.com.bulat.trackergps.location

import org.osmdroid.util.GeoPoint
import java.io.Serializable

data class LocationModel(
    val velocity: Float = 0.0f,
    val distance: Float = 0.0f,
    val geoPointsList: ArrayList<GeoPoint>,
) : Serializable
