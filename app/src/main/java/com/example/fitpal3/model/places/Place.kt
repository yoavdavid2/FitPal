package com.example.fitpal3.model.places

data class Place(
    val displayName: DisplayName,
    val formattedAddress: String?,
    val location: LatLng,
    val types: List<String>?,
    val rating: Float?
)