package ru.com.bulat.trackergps.utils

import org.osmdroid.api.IGeoPoint
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.TileSystem
import org.osmdroid.views.MapView


object ZoomUtil {
    fun zoomTo(mapView: MapView, min: IGeoPoint, max: IGeoPoint) {
        val tileProvider = mapView.tileProvider
        val controller = mapView.controller
        val center: IGeoPoint =
            GeoPoint((max.latitudeE6 + min.latitudeE6) / 2, (max.longitudeE6 + min.longitudeE6) / 2)


        // diagonale in pixels
        val pixels =
            Math.sqrt((mapView.width * mapView.width + mapView.height * mapView.height).toDouble())
        val requiredMinimalGroundResolutionInMetersPerPixel =
            GeoPoint(min.latitudeE6, min.longitudeE6).distanceToAsDouble(max) as Double / pixels
        val zoom = calculateZoom(
            center.latitude,
            requiredMinimalGroundResolutionInMetersPerPixel,
            tileProvider.maximumZoomLevel,
            tileProvider.minimumZoomLevel
        )
        controller.setZoom(zoom)
        controller.setCenter(center)
    }

    private fun calculateZoom(
        latitude: Double,
        requiredMinimalGroundResolutionInMetersPerPixel: Double,
        maximumZoomLevel: Int,
        minimumZoomLevel: Int
    ): Int {
        for (zoom in maximumZoomLevel downTo minimumZoomLevel) {
            if (TileSystem.GroundResolution(
                    latitude,
                    zoom
                ) > requiredMinimalGroundResolutionInMetersPerPixel
            ) return zoom
        }
        return 0
    }
}