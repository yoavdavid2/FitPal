package com.example.fitpal.model.networking

import com.example.fitpal.BuildConfig
import com.example.fitpal.model.networking.authenticators.GeminiInterceptor
import com.example.fitpal.model.networking.authenticators.PlacesInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkingClient {

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(PlacesInterceptor())
            .build()
    }

    private val geminiHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(GeminiInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
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

    val GeminiApiClient: GeminiApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.GEMINI_API_URL)
            .client(geminiHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(GeminiApi::class.java)
    }
}