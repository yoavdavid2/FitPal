package com.example.fitpal.model.networking

import com.example.fitpal.BuildConfig
import com.example.fitpal.model.networking.authenticators.PlacesInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkingClient {

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(PlacesInterceptor())
            .build()
    }

    val PlacesApiClient: PlacesApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.PLACES_API_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(PlacesApi::class.java)
    }
}