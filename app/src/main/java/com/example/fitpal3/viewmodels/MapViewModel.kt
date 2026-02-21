package com.example.fitpal3.viewmodels

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
import com.example.fitpal3.R
import com.example.fitpal3.model.places.MarkerOptionsWithPlace
import com.example.fitpal3.model.places.Place
import com.example.fitpal3.repositories.PlacesRepository
import com.example.fitpal3.utils.MarkerIconFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import androidx.core.graphics.toColorInt

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val _currentLocation = MutableLiveData<LatLng?>()
    val currentLocation: LiveData<LatLng?> = _currentLocation

    private val _markers = MutableLiveData<List<MarkerOptionsWithPlace>>()
    val markers: LiveData<List<MarkerOptionsWithPlace>> = _markers

    private val _selectedGym = MutableLiveData<Place?>()
    val selectedGym: LiveData<Place?> = _selectedGym

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isLoadingDetails = MutableLiveData<Boolean>()
    val isLoadingDetails: LiveData<Boolean> = _isLoadingDetails

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)
    private val placesRepository = PlacesRepository()
    private val placesByMarkerId = mutableMapOf<String, Place>()

    init {
        _currentLocation.value = LatLng(0.0, 0.0)
        _markers.value = listOf(
            MarkerOptionsWithPlace(
                MarkerOptions()
                    .position(_currentLocation.value!!)
                    .title("Default Location"),
                null
            )
        )
        _isLoading.value = false
        _isLoadingDetails.value = false
    }

    fun hasLocationPermissions(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun getUserLocation(context: Context, showPermissionError: Boolean = true) {
        Log.d("mapFragment", "Starting locations search")
        _isLoading.value = true

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
            }.addOnFailureListener {
                _errorMessage.postValue("Failed to get current location")
                _isLoading.postValue(false)
            }
        } else if (showPermissionError) {
            _errorMessage.postValue("Location permission not granted")
            _isLoading.postValue(false)
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun getNearbyGyms(userLocation: LatLng) {
        Log.d("mapFragment", "Starting gyms search")

        placesRepository.findNearbyGyms(
            latitude = userLocation.latitude,
            longitude = userLocation.longitude,
            radiusInMeters = 10000,
            callback = object : PlacesRepository.NearbyGymsCallback {
                override fun onSuccess(gyms: List<Place>) {
                    if (gyms.isEmpty()) {
                        _errorMessage.postValue("No gyms found within 5km radius")
                    } else {
                        updateGymMarkers(gyms, userLocation)
                    }
                    _isLoading.postValue(false)
                }

                override fun onError(error: Exception) {
                    _errorMessage.postValue("Error finding gyms: ${error.message}")
                    _isLoading.postValue(false)
                }
            }
        )
    }

    private fun updateGymMarkers(gyms: List<Place>, userLocation: LatLng) {
        placesByMarkerId.clear()

        val userMarker = MarkerOptionsWithPlace(
            MarkerOptions()
                .position(userLocation)
                .title("Your Location")
                .snippet("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker()),
            null
        )

        val gymMarkers = gyms.map { gym ->
            val color = when {
                gym.types?.contains("fitness_center") == true -> "#34A853".toColorInt()
                gym.types?.contains("gym") == true -> "#00BCD4".toColorInt()
                else -> "#95A5A6".toColorInt()
            }

            val overlayRes = when {
                gym.types?.contains("fitness_center") == true -> R.drawable.fitness
                gym.types?.contains("gym") == true -> R.drawable.gym
                else -> BitmapDescriptorFactory.defaultMarker(color.toFloat())
            }

            val icon = MarkerIconFactory.createPinnedIcon(
                context = getApplication(),
                baseRes = R.drawable.marker_pin_base,
                overlayRes = overlayRes as Int,
                baseTint = color,
                overlayTint = 0xFFFFFFFF.toInt()
            )

            MarkerOptionsWithPlace(
                MarkerOptions()
                    .position(
                        LatLng(
                            gym.location.latitude,
                            gym.location.longitude
                        )
                    )
                    .title(gym.displayName.text)
                    .snippet(gym.formattedAddress ?: "")
                    .icon(icon),
                gym
            )

        }

        _markers.postValue(listOf(userMarker) + gymMarkers)
    }

    fun onMarkerSelected(markerId: String) {
        _selectedGym.value = placesByMarkerId[markerId]
    }

    fun registerMarker(markerId: String, place: Place?) {
        if (place != null) {
            placesByMarkerId[markerId] = place
        }
    }

    fun clearSelectedGym() {
        _selectedGym.value = null
    }
}