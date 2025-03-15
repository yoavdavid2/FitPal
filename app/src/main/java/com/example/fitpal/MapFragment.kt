package com.example.fitpal

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.fitpal.databinding.FragmentMapBinding
import com.example.fitpal.viewmodels.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {
    private var binding: FragmentMapBinding? = null
    private var map: GoogleMap? = null
    private val viewModel: MapViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        Log.d(TAG, "visible")
        binding?.progressBar?.visibility = View.VISIBLE
        return binding?.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (!viewModel.hasLocationPermissions(requireContext())) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            viewModel.getUserLocation(requireContext())
        }

        observeViewModel()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        setupMapSettings()

//        if (checkLocationPermissions()) {
//            enableMyLocation()
//            viewModel.startLocationUpdates()
//        } else {
//            requestLocationPermissions()
//        }

        viewModel.getUserLocation(requireContext())
        Log.d(TAG, "finished")

        binding?.progressBar?.visibility = View.GONE
    }

    private fun observeViewModel() {
        viewModel.currentLocation.observe(viewLifecycleOwner) { location ->
            location?.let {
                map?.addMarker(
                    MarkerOptions()
                        .position(location)
                        .title("Current location")
                        .snippet("You are here")
                )
                moveCamera(it, 12f)
                drawCircles(it)
            }
        }
        viewModel.markers.observe(viewLifecycleOwner) { markers ->
            for (marker in markers) {
                map?.addMarker(marker)
            }
        }
    }

//    private fun updateLocationOnMap(location: LatLng) {
//        if (currentLocationMarker == null) {
//            currentLocationMarker = map?.addMarker(
//                MarkerOptions()
//                    .position(location)
//                    .title("Current location")
//                    .snippet("You are here")
//            )
//        } else {
//            currentLocationMarker?.position = location
//        }
//    }

    private fun moveCamera(location: LatLng, zoomLevel: Float) {
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel))
    }

    private fun drawCircles(center: LatLng) {
        map?.addCircle(
            CircleOptions()
                .center(center)
                .radius(5000.0)
                .strokeColor(Color.GREEN)
                .strokeWidth(2f)
                .fillColor(Color.argb(30, 0, 255, 0))
        )
        map?.addCircle(
            CircleOptions()
                .center(center)
                .radius(10000.0)
                .strokeColor(Color.RED)
                .strokeWidth(2f)
                .fillColor(Color.argb(20, 255, 0, 0))
        )
    }

    private fun setupMapSettings() {
        map?.apply {
            uiSettings.apply {
                isZoomControlsEnabled = true
                isZoomGesturesEnabled = true
                isCompassEnabled = true
                isMyLocationButtonEnabled = true
//                isBuildingsEnabled = true
            }

            try {
                val success = setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireContext(), R.raw.map_style
                    )
                )
                if (!success) {
                    Log.e(TAG, "Error loading map style")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception caught: ${e.message}")
            }
        }
    }

    companion object {
        private const val TAG = "mapFragment"
    }
}