package com.example.fitpal3.repositories

import android.util.Log
import com.example.fitpal3.BuildConfig
import com.example.fitpal3.model.networking.NetworkingClient
import com.example.fitpal3.model.places.Circle
import com.example.fitpal3.model.places.LatLng
import com.example.fitpal3.model.places.LocationRestriction
import com.example.fitpal3.model.places.Place
import com.example.fitpal3.model.places.PlacesApiRequest
import com.google.gson.Gson
import java.util.concurrent.Executors

class PlacesRepository {
    private val placesApiService = NetworkingClient.PlacesApiClient
    private val executor = Executors.newSingleThreadExecutor()
    private val gson = Gson()

    interface NearbyGymsCallback {
        fun onSuccess(gyms: List<Place>)
        fun onError(error: Exception)
    }

    fun findNearbyGyms(
        latitude: Double,
        longitude: Double,
        radiusInMeters: Int = 10000,
        language: String = "he",
        callback: NearbyGymsCallback
    ) {
        executor.execute {
            try {
                val requestBody = PlacesApiRequest(
                    locationRestriction = LocationRestriction(
                        circle = Circle(
                            center = LatLng(
                                latitude = latitude,
                                longitude = longitude
                            ),
                            radius = radiusInMeters.toDouble()
                        )
                    ),
                    includedTypes = listOf("gym", "fitness_center"),
                    languageCode = language
                )

                Log.d("PlacesRepository", "Request: ${gson.toJson(requestBody)}")

                val call = placesApiService.searchNearbyGyms(
                    apiKey = BuildConfig.GOOGLE_MAPS_API_KEY,
                    request = requestBody
                )

                val response = call.execute()

                if (response.isSuccessful) {
                    val placesResponse = response.body()
                    val places = placesResponse?.places.orEmpty()

                    if (placesResponse == null) {
                        callback.onError(Exception("Response body is null"))
                        return@execute
                    }

                    Log.d("PlacesRepository", "Found ${places.size} gyms")

                    if (places.isNotEmpty()) {
                        callback.onSuccess(places)
                    } else {
                        callback.onError(Exception("No gyms found"))
                    }
                } else {
                    val errorMsg = "API Error: ${response.code()} - ${response.message()}"
                    callback.onError(Exception(errorMsg))
                }

            } catch (e: Exception) {
                Log.e("PlacesRepository", "Exception finding gyms: ${e.message}")
                callback.onError(e)
            }
        }
    }
}