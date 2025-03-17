package com.example.fitpal

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresPermission
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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {
    private var binding: FragmentMapBinding? = null
    private var map: GoogleMap? = null
    private val viewModel: MapViewModel by viewModels()
    private val markedLocations = mutableListOf<Marker>()

    private var permissionsRequested = false
    private var hasReceivedLocation = false
    private var isMapReady = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        Log.d(TAG, "MapFragment created")
        return binding?.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        observeViewModel()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        isMapReady = true
        setupMapSettings()

        map?.clear()

        requestLocationIfNeeded()
        Log.d(TAG, "Map ready")
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun requestLocationIfNeeded() {
        if (viewModel.hasLocationPermissions(requireContext())) {
            viewModel.getUserLocation(requireContext(), showPermissionError = !hasReceivedLocation)
        } else if (!permissionsRequested) {
            permissionsRequested = true
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isVisibleToUser && isMapReady) {
            Log.d(TAG, "MapFragment becomes visible, refreshing")
            refreshMap()
        }
    }

    override fun onResume() {
        super.onResume()
        if (userVisibleHint && isMapReady) {
            Log.d(TAG, "MapFragment resumed and visible, refreshing")
            refreshMap()
        }
    }

    fun refreshMap() {
        if (viewModel.hasLocationPermissions(requireContext())) {
            viewModel.getUserLocation(requireContext(), showPermissionError = false)
        }
    }

    private fun observeViewModel() {
        viewModel.apply {
            currentLocation.observe(viewLifecycleOwner) { location ->
                location?.let {
                    hasReceivedLocation = true
                    if (isMapReady) {
                        moveCamera(it, 12f)
                        drawCircles(it)
                    }
                }
            }

            markers.observe(viewLifecycleOwner) { markersOptions ->
                if (isMapReady) {
                    updateMarkers(markersOptions)
                }
            }

            isLoading.observe(viewLifecycleOwner) { isLoading ->
                binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
            }

            errorMessage.observe(viewLifecycleOwner) { errorMessage ->
                errorMessage?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun updateMarkers(markersOptions: List<MarkerOptions>) {
        markedLocations.apply {
            forEach { it.remove() }
            clear()
        }

        markersOptions.forEach { marker ->
            map?.addMarker(marker)?.let {
                markedLocations.add(it)
            }
        }
    }

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
                .strokeColor(Color.YELLOW)
                .strokeWidth(2f)
                .fillColor(Color.argb(20, 255, 255, 0))
        )
    }

    @SuppressLint("MissingPermission")
    private fun setupMapSettings() {
        map?.apply {
            uiSettings.apply {
                isZoomControlsEnabled = true
                isZoomGesturesEnabled = true
                isCompassEnabled = true
                isMyLocationEnabled = viewModel.hasLocationPermissions(requireContext())
                isMyLocationButtonEnabled = viewModel.hasLocationPermissions(requireContext())
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

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupMapSettings()
                    viewModel.getUserLocation(requireContext())
                } else {
                    // Permission denied
                    Toast.makeText(
                        requireContext(),
                        "Location permission is required to show gyms near you",
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        markedLocations.apply {
            forEach { it.remove() }
            clear()
        }

        binding = null
    }

    companion object {
        private const val TAG = "mapFragment"
    }
}