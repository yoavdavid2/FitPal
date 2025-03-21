package com.example.fitpal.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class GPSService : Service() {
    private val binder = LocalBinder()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var googleLocationCallback: LocationCallback? = null
    private val serviceCallbacks = mutableSetOf<GPSCallback>()
    private var isTrackingPaused = false
    private var locationUpdateRequest: LocationRequest? = null
    private val TAG = "GPSService"

    interface GPSCallback {
        fun onLocationUpdate(latitude: Double, longitude: Double)
    }

    inner class LocalBinder : Binder() {
        fun getService(): GPSService = this@GPSService
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createLocationRequest()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    fun startLocationUpdates(callback: GPSCallback) {
        serviceCallbacks.add(callback)

        Log.d(TAG, "Starting location updates, active callbacks: ${serviceCallbacks.size}")

        if (isTrackingPaused) {
            resumeLocationTracking()
        } else if (googleLocationCallback == null) {
            startLocationTracking()
        } else {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        callback.onLocationUpdate(it.latitude, it.longitude)
                    }
                }
            } catch (e: SecurityException) {
                Log.e(TAG, "Error getting last location: ${e.message}")
            }
        }
    }

    fun pauseLocationUpdates() {
        if (googleLocationCallback != null && !isTrackingPaused) {
            Log.d(TAG, "Pausing location updates")
            try {
                googleLocationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
                isTrackingPaused = true
            } catch (e: Exception) {
                Log.e(TAG, "Error pausing location updates: ${e.message}")
            }
        }
    }

    private fun resumeLocationTracking() {
        if (isTrackingPaused && serviceCallbacks.isNotEmpty()) {
            Log.d(TAG, "Resuming location tracking")
            startLocationTracking()
            isTrackingPaused = false
        }
    }

    fun stopLocationUpdates() {
        Log.d(TAG, "Stopping all location updates")
        serviceCallbacks.clear()

        if (googleLocationCallback != null) {
            try {
                googleLocationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
                googleLocationCallback = null
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping location updates: ${e.message}")
            }
        }

        isTrackingPaused = false
    }

    private fun createLocationRequest() {
        locationUpdateRequest = LocationRequest.Builder(UPDATE_INTERVAL_MS)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMinUpdateIntervalMillis(FASTEST_UPDATE_INTERVAL_MS)
            .setMaxUpdateDelayMillis(MAX_UPDATE_DELAY_MS)
            .build()
    }

    private fun startLocationTracking() {
        if (googleLocationCallback == null) {
            googleLocationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation?.let { location ->
                        serviceCallbacks.forEach { callback ->
                            callback.onLocationUpdate(location.latitude, location.longitude)
                        }
                    }
                }
            }
        }

        try {
            googleLocationCallback?.let {
                locationUpdateRequest?.let { request ->
                    fusedLocationClient.requestLocationUpdates(
                        request,
                        it,
                        Looper.getMainLooper()
                    )
                    Log.d(TAG, "Location tracking started")
                }
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Error requesting location updates: ${e.message}")
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "Service destroyed")
        googleLocationCallback?.let {
            try {
                fusedLocationClient.removeLocationUpdates(it)
            } catch (e: Exception) {
                Log.e(TAG, "Error removing location updates on destroy: ${e.message}")
            }
        }
        googleLocationCallback = null
        serviceCallbacks.clear()
        isTrackingPaused = false
        super.onDestroy()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "Service unbound")
        return super.onUnbind(intent)
    }

    companion object {
        private const val UPDATE_INTERVAL_MS = 10000L // 10 seconds
        private const val FASTEST_UPDATE_INTERVAL_MS = 5000L // 5 seconds
        private const val MAX_UPDATE_DELAY_MS = 15000L // 15 seconds
    }
}