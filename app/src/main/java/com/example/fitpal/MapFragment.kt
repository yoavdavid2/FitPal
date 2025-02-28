package com.example.fitpal

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.fitpal.databinding.FragmentMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {
    private var binding: FragmentMapBinding? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var map: GoogleMap? = null
    private var currentLocation: LatLng? = null
    private var gpsService: GPSService? = null
    private var isBound = false
    private var didUserMoveMap = false
    private var isMapReady = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.progressBar?.visibility = View.VISIBLE

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        // Get the map fragment and initialize it
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        if (checkLocationPermissions()) {
            bindGPSService()
            getCurrentLocation()
        } else {
            requestLocationPermissions()
        }
    }

    private fun getCurrentLocation() {
        if (checkLocationPermissions()) {
            try {
                fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                    binding?.progressBar?.visibility = View.GONE

                    if (location != null) {
                        currentLocation = LatLng(location.latitude, location.longitude)

                        if (isMapReady) {
                            updateMapWithLocation()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(), "Location Unavailable", Toast.LENGTH_SHORT
                        ).show()
                    }
                }.addOnFailureListener { e ->
                    binding?.progressBar?.visibility = View.GONE
                    Toast.makeText(
                        requireContext(), "Error getting location: ${e.message}", Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: SecurityException) {
                binding?.progressBar?.visibility = View.GONE
                Toast.makeText(
                    requireContext(), "Location permission error: ${e.message}", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateMapWithLocation() {
        map?.let { googleMap ->
            currentLocation?.let { location ->
                binding?.progressBar?.visibility = View.GONE
                googleMap.clear()
                googleMap.addMarker(MarkerOptions().position(location).title("Current Location"))

                if (!didUserMoveMap) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
                }
            } ?: run {
                binding?.progressBar?.visibility = View.VISIBLE
            }
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as GPSService.LocalBinder
            gpsService = binder.getService()
            isBound = true

            gpsService?.startLocationUpdates(locationCallback)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            gpsService = null
            isBound = false
        }
    }

    private val locationCallback = object : GPSService.GPSCallback {
        override fun onLocationUpdate(latitude: Double, longitude: Double) {
            val newLocation = LatLng(latitude, longitude)

            if (currentLocation == null ||
                calculateDistance(currentLocation!!, newLocation) > MIN_DISTANCE_METERS
            ) {
                currentLocation = newLocation
                if (isMapReady) {
                    updateMapWithLocation()
                }
            }
        }
    }

    private fun calculateDistance(loc1: LatLng, loc2: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            loc1.latitude,
            loc1.longitude,
            loc2.latitude,
            loc2.longitude,
            results
        )
        return results[0]
    }

    private fun bindGPSService() {
        Intent(requireContext(), GPSService::class.java).also { intent ->
            requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun requestLocationPermissions() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            ), LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun checkLocationPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bindGPSService()
                getCurrentLocation()
            } else {
                binding?.progressBar?.visibility = View.GONE
                Toast.makeText(
                    requireContext(), "Location permission denied", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        try {
            map = googleMap
            isMapReady = true

            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

            googleMap.uiSettings.apply {
                isZoomControlsEnabled = true
                isCompassEnabled = true
                isMyLocationButtonEnabled = true
            }

            if (checkLocationPermissions()) {
                try {
                    googleMap.isMyLocationEnabled = true
                } catch (e: SecurityException) {
                    Toast.makeText(
                        requireContext(),
                        "Cannot enable location layer: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            googleMap.setOnCameraMoveStartedListener { reason ->
                if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                    didUserMoveMap = true
                }
            }

            googleMap.setOnMyLocationButtonClickListener {
                didUserMoveMap = false
                // Return false to allow default behavior (centering on location)
                false
            }

            val defaultLocation = LatLng(0.0, 0.0)  // Replace with a sensible default
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 2f))

            updateMapWithLocation()
        } catch (e: Exception) {
            binding?.progressBar?.visibility = View.GONE
            Toast.makeText(
                requireContext(),
                "Map initialization failed: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onPause() {
        super.onPause()
        gpsService?.pauseLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        if (isBound) {
            gpsService?.startLocationUpdates(locationCallback)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isBound) {
            gpsService?.stopLocationUpdates()
            requireContext().unbindService(serviceConnection)
            isBound = false
        }
        map = null
        isMapReady = false
        binding = null
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val MIN_DISTANCE_METERS = 10f
    }
}