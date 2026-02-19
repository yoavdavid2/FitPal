package com.example.fitpal3.utils.extensions

import android.location.Location
import com.google.android.gms.maps.model.LatLng


/**
 * Extension function for Double that checks if a point is within a circular radius.
 *
 * @param center The center point of the circle (LatLng)
 * @param point The point to check if it's within the radius (LatLng)
 * @return true if the distance between center and point is less than or equal to this Double value (representing the radius in meters)
 *
 * Example usage:
 * ```
 * val centerPoint = LatLng(37.7749, -122.4194)
 * val pointToCheck = LatLng(37.7735, -122.4217)
 *
 * if (5000.0.isWithinRadius(centerPoint, pointToCheck)) {
 *     // Point is within 5km of the center
 * }
 * ```
 */
fun Double.isWithinRadius(
    center: LatLng,
    point: LatLng,
): Boolean {
    val results = FloatArray(1)
    Location.distanceBetween(
        center.latitude,
        center.longitude,
        point.latitude,
        point.longitude,
        results
    )
    return results[0] <= this

}