package com.example.fitpal.model.networking

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class GeminiRequest(
    val contents: List<Content>,
    @SerializedName("generation_config") val generationConfig: GenerationConfig
)

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String
)

data class GenerationConfig(
    val temperature: Float,
    @SerializedName("top_k") val topK: Int,
    @SerializedName("top_p") val topP: Float,
    @SerializedName("max_output_tokens") val maxOutputTokens: Int
)

data class GeminiResponse(
    val candidates: List<Candidate>
)

data class Candidate(
    val content: Content
)

interface GeminiApi {
    @POST("v1/models/gemini-1.5-pro:generateContent")
    fun generateContent(@Body request: GeminiRequest): Call<GeminiResponse>
}