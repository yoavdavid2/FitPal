package com.example.fitpal3

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.fitpal3.databinding.FragmentMapBinding
import com.example.fitpal3.model.places.MarkerOptionsWithPlace
import com.example.fitpal3.model.places.Place
import com.example.fitpal3.utils.extensions.toCapital
import com.example.fitpal3.viewmodels.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.core.net.toUri

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private var binding: FragmentMapBinding? = null
    private var map: GoogleMap? = null
    private val viewModel: MapViewModel by viewModels()
    private val markedGyms = mutableListOf<Marker>()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private var bottomSheetCallback: BottomSheetBehavior.BottomSheetCallback? = null

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

        setupBottomSheet()
        observeViewModel()
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

            selectedGym.observe(viewLifecycleOwner) { gym ->
                updateBottomSheet(gym)
            }

            isLoading.observe(viewLifecycleOwner) { isLoading ->
                binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
            }

            isLoadingDetails.observe(viewLifecycleOwner) { isLoadingDetails ->
                binding?.gymDetailsBottomSheet?.detailsProgressBar?.visibility =
                    if (isLoadingDetails) View.VISIBLE else View.GONE
            }

            errorMessage.observe(viewLifecycleOwner) { errorMessage ->
                errorMessage?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onResume() {
        super.onResume()
        if (userVisibleHint && isMapReady) {
            Log.d(TAG, "MapFragment resumed and visible, refreshing")
            refreshMap()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        bottomSheetCallback?.let {
            bottomSheetBehavior.removeBottomSheetCallback(it)
        }
        bottomSheetCallback = null

        viewModel.clearSelectedGym()

        markedGyms.apply {
            forEach { it.remove() }
            clear()
        }

        binding = null
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isVisibleToUser && isMapReady) {
            Log.d(TAG, "MapFragment becomes visible, refreshing")
            refreshMap()
        }
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

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        isMapReady = true
        setupMapSettings()

        map?.apply {
            clear()
            setOnMarkerClickListener(this@MapFragment)
            setOnMapClickListener {
                hideBottomSheet()
            }
        }

        viewModel.markers.value?.let { marker ->
            updateMarkers(marker)
        }
        requestLocationIfNeeded()
        Log.d(TAG, "Map ready")
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
    private fun refreshMap() {
        if (viewModel.hasLocationPermissions(requireContext())) {
            viewModel.getUserLocation(requireContext(), showPermissionError = false)
        }
    }

    private fun setupBottomSheet() {
        val bottomSheet = binding?.gymDetailsBottomSheet?.gymBottomSheet
        if (bottomSheet != null) {
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            bottomSheetCallback = object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        viewModel.clearSelectedGym()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    // slideOffset ranges from -1 to 1:
                    // -1 represents a hidden sheet
                    // 0 represents a collapsed sheet
                    // 1 represents a fully expanded sheet

                    if (slideOffset >= 0) {
                        binding?.gymDetailsBottomSheet?.apply {
                            expandedContent.alpha = slideOffset

                            val rotation = slideOffset * 180f
                            bottomSheetHandle.rotation = rotation

                            val maxElevation = 16f // dp
                            bottomSheet.elevation = slideOffset * maxElevation

                            val startColor = ContextCompat.getColor(
                                requireContext(),
                                android.R.color.darker_gray
                            )
                            val endColor = ContextCompat.getColor(
                                requireContext(),
                                android.R.color.holo_blue_light
                            )

                            val blendedColor = blendColors(startColor, endColor, slideOffset)
                            bottomSheetHandle.setBackgroundColor(blendedColor)
                        }
                    } else {
                        binding?.gymDetailsBottomSheet?.apply {
                            expandedContent.alpha = 0f
                            bottomSheetHandle.rotation = 0f
                            bottomSheet.elevation = 1f // minimal elevation

                            val defaultColor = ContextCompat.getColor(
                                requireContext(),
                                android.R.color.darker_gray
                            )
                            bottomSheetHandle.setBackgroundColor(defaultColor)
                        }
                    }
                }

                private fun blendColors(startColor: Int, endColor: Int, ratio: Float): Int {
                    val inverseRatio = 1f - ratio

                    val r =
                        (Color.red(startColor) * inverseRatio + Color.red(endColor) * ratio).toInt()
                    val g =
                        (Color.green(startColor) * inverseRatio + Color.green(endColor) * ratio).toInt()
                    val b =
                        (Color.blue(startColor) * inverseRatio + Color.blue(endColor) * ratio).toInt()

                    return Color.rgb(r, g, b)
                }
            }

            bottomSheetCallback?.let { bottomSheetBehavior.addBottomSheetCallback(it) }


            binding?.gymDetailsBottomSheet?.directionsButton?.setOnClickListener {
                viewModel.selectedGym.value?.let { gym ->
                    openDirections(gym)
                }
            }
        }
    }

    private fun updateBottomSheet(place: Place?) {
        if (place == null) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            return
        }

        binding?.apply {
            gymDetailsBottomSheet.apply {
                gymName.text = place.displayName.text
                gymAddress.text = place.formattedAddress ?: "Address unavailable"
                gymRating.text = place.rating?.toString() ?: "N/A"
                ratingBar.rating = place.rating ?: 0f
                gymTypes.text = place.types?.joinToString(", ") {
                    it.replace("_", " ").toCapital()
                } ?: "Information unavailable"
            }
        }

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun hideBottomSheet() {
        if (::bottomSheetBehavior.isInitialized) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            viewModel.clearSelectedGym()
        }
    }

    private fun openDirections(place: Place) {
        val uri =
            "geo:0,0?q=${place.location.latitude},${place.location.longitude}(${place.displayName.text})".toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")

        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "Google Maps not installed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateMarkers(markersOptions: List<MarkerOptionsWithPlace>) {
        markedGyms.apply {
            forEach { it.remove() }
            clear()
        }

        markersOptions.forEach { marker ->
            val mark = map?.addMarker(marker.markerOptions)
            mark?.let {
                markedGyms.add(it)
                viewModel.registerMarker(it.id, marker.place)
            }
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        moveCamera(marker.position, 14f)
        viewModel.onMarkerSelected(marker.id)
        return true
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

    companion object {
        private const val TAG = "mapFragment"
    }
}