package com.example.fitpal.model.places

import com.google.android.gms.maps.model.MarkerOptions

data class MarkerOptionsWithPlace(
    val markerOptions: MarkerOptions,
    val place: Place?,
)