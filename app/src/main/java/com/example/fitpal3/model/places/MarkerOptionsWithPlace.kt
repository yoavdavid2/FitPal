package com.example.fitpal3.model.places

import com.google.android.gms.maps.model.MarkerOptions

data class MarkerOptionsWithPlace(
    val markerOptions: MarkerOptions,
    val place: Place?,
)