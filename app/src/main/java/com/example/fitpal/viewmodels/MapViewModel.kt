package com.example.fitpal.viewmodels

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.fitpal.utils.extensions.isWithinRadius
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val _currentLocation = MutableLiveData<LatLng?>()
    val currentLocation: LiveData<LatLng?> = _currentLocation

    private val _markers = MutableLiveData<List<MarkerOptions>>()
    val markers: LiveData<List<MarkerOptions>> = _markers

    private val _zoomLevel = MutableLiveData<Float>()
    val zoomLevel: LiveData<Float> = _zoomLevel

    private val placesClient: PlacesClient = Places.createClient(application)
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    init {
        _currentLocation.value = LatLng(-34.0, 151.0)
        _zoomLevel.value = 12f
        _markers.value = listOf(
            MarkerOptions()
                .position(_currentLocation.value!!)
                .title("San Fransisco")
        )
    }

    fun updateCurrentLocation(latLng: LatLng) {
        _currentLocation.value = latLng
    }

    fun hasLocationPermissions(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun getUserLocation(context: Context) {
        Log.d("mapFragment", "Starting locations search")
        if (hasLocationPermissions(context)) {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).addOnSuccessListener { location ->
                location?.let {
                    val current = LatLng(it.latitude, it.longitude)
                    _currentLocation.postValue(current)
                    getNearbyGyms(current)
                }
            }.addOnFailureListener { }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun getNearbyGyms(userLocation: LatLng) {
        Log.d("mapFragment", "Starting gyms search")
        val placesRequest =
            FindCurrentPlaceRequest.newInstance(listOf(Place.Field.NAME, Place.Field.LAT_LNG))

        placesClient.findCurrentPlace(placesRequest)
            .addOnSuccessListener { response ->
                val gyms = response.placeLikelihoods.mapNotNull { placeLikelihood ->
                    placeLikelihood.place.latLng?.let { latLng ->
                        Log.d("mapFragment", "Color checking")
                        val color = if (5000.0.isWithinRadius(
                                userLocation,
                                latLng
                            )
                        ) BitmapDescriptorFactory.HUE_GREEN else BitmapDescriptorFactory.HUE_ORANGE
                        Log.d("mapFragment", "Color checking finished")
                        return@let MarkerOptions()
                            .position(latLng)
                            .title(placeLikelihood.place.name)
                            .icon(BitmapDescriptorFactory.defaultMarker(color))
                    }
                }
                _markers.postValue(gyms)
            }
    }

}