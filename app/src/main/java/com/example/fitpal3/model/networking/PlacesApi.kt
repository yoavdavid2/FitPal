package com.example.fitpal3.model.networking

import com.example.fitpal3.model.places.PlacesApiRequest
import com.example.fitpal3.model.places.PlacesApiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface PlacesApi {
    @POST("v1/places:searchNearby")
    fun searchNearbyGyms(
        @Query("key") apiKey: String,
        @Query("prettyPrint") prettyPrint: Boolean = true,
        @Body request: PlacesApiRequest
    ) : Call<PlacesApiResponse>
}
