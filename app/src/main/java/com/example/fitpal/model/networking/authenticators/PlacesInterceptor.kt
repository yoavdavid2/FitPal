package com.example.fitpal.model.networking.authenticators

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class PlacesInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader(
                "X-Goog-FieldMask",
                "places.displayName,places.formattedAddress,places.location,places.types,places.rating"
            )
            .build()

        Log.d("PlacesInterceptor", "Request URL: ${request.url()}")
        Log.d("PlacesInterceptor", "Headers: ${request.headers()}")
        return chain.proceed(request)
    }

}
