package com.example.fitpal.model

import com.example.fitpal.model.places.Place
import com.google.android.gms.maps.model.MarkerOptions

data class MarkerOptionsWithPlace(
    val markerOptions: MarkerOptions,
    val place: Place?,
)
