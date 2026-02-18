package com.example.fitpal.model.places

import com.google.gson.annotations.SerializedName

data class PlacesApiRequest(
    val locationRestriction: LocationRestriction,
    val includedTypes: List<String>,
    val languageCode: String
)

data class PlacesApiResponse(
    @SerializedName("places")
    val places: List<Place>? = null
)

data class LocationRestriction(
    val circle: Circle
)

data class Circle(
    val center: LatLng,
    val radius: Double
)

data class LatLng(
    val latitude: Double,
    val longitude: Double
)

data class DisplayName(
    val text: String,
    val languageCode: String
)