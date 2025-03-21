package com.example.fitpal.services

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.fitpal.BuildConfig
import com.example.fitpal.model.fitness.entities.Article
import com.example.fitpal.model.fitness.entities.Tip
import com.example.fitpal.model.fitness.entities.WorkoutPlan
import com.example.fitpal.model.networking.Content
import com.example.fitpal.model.networking.GeminiRequest
import com.example.fitpal.model.networking.GeminiResponse
import com.example.fitpal.model.networking.GenerationConfig
import com.example.fitpal.model.networking.NetworkingClient
import com.example.fitpal.model.networking.Part
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date
import java.util.UUID
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.regex.Pattern


class GeminiService private constructor() {
    private val apiKey = BuildConfig.GEMINI_API_KEY
    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val mainHandler: Handler = Handler(Looper.getMainLooper())

    companion object {
        @Volatile
        private var instance: GeminiService? = null

        fun getInstance(requireContext: Context): GeminiService {
            return instance ?: synchronized(this) {
                instance ?: GeminiService().also { instance = it }
            }
        }
    }

    interface ContentCallback {
        fun onSuccess(tips: List<Tip>, articles: List<Article>, workoutPlans: List<WorkoutPlan>)
        fun onError(error: String)
    }

    fun generateContent(callback: ContentCallback) {
        executor.execute {
            Log.d("GeminiService", "Generating content...")
            val prompt = """
                Generate valid JSON containing fitness tips, articles, and workout plans.
                
                Your response MUST be a valid JSON object with the EXACT structure below:
                
                {
                  "tips": [
                    {"title": "Tip title", "content": "Tip content"},
                    {"title": "Another tip", "content": "Another tip content"},
                    {"title": "Third tip", "content": "Third tip content"}
                  ],
                  "articles": [
                    {"title": "Article title", "content": "Article content", "category": "Category name", "imageUrl": "url to related picture"},
                    {"title": "Second article", "content": "Second article content", "category": "Another category", "imageUrl": "url to another related picture"}
                  ],
                  "workoutPlans": [
                    {"title": "Workout plan title", "content": "Workout plan details", "difficulty": "beginner", "duration": "30 minutes", "targetMuscleGroup": "Core"},
                    {"title": "Another workout", "content": "Another workout details", "difficulty": "intermediate", "duration": "45 minutes", "targetMuscleGroup": "Upper body"}
                  ]
                }
                
                Generate exactly 3 fitness tips, 2 articles about fitness, and 2 workout plans.
                Keep tips short (1-2 sentences), articles medium length (2-3 paragraphs), and workout plans should be detailed enough to follow.
                For each article, find an image that is related and add it's url so it can be presented alongside the article itself.
                
                Important: DO NOT include ANY explanatory text before or after the JSON. Your entire response must be ONLY the JSON object
            """.trimIndent()

            Log.d("GeminiService", "Creating request with prompt: ${prompt.take(100)}...")
            val request = GeminiRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                generationConfig = GenerationConfig(
                    temperature = 0.7f,
                    topK = 40,
                    topP = 0.95f,
                    maxOutputTokens = 1024
                )
            )

            Log.d("GeminiService", "Sending request to Gemini API...")
            NetworkingClient.GeminiApiClient.generateContent(request)
                .enqueue(object : Callback<GeminiResponse> {
                    override fun onResponse(
                        call: Call<GeminiResponse>,
                        response: Response<GeminiResponse>,
                    ) {
                        Log.d(
                            "GeminiService",
                            "Received response. Success: ${response.isSuccessful}, Code: ${response.code()}"
                        )
                        if (response.isSuccessful) {
                            try {
                                val textResponse =
                                    response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                                Log.d("GeminiService", "Response text: ${textResponse?.take(100)}")

                                if (textResponse != null) {
                                    val matcher = Pattern.compile("\\{.*\\}", Pattern.DOTALL).matcher(textResponse)

                                    if (matcher.find()) {
                                        val jsonPart = matcher.group(0)
                                        Log.d("GeminiService", "Extracted JSON part: $jsonPart")

                                        try {
                                            val gson = Gson()
                                            val jsonObject =
                                                gson.fromJson(jsonPart, JsonObject::class.java)

                                            if (jsonObject.has("tips") && jsonObject.has("articles") && jsonObject.has(
                                                    "workoutPlans"
                                                )
                                            ) {
                                                val tips = parseTips(jsonObject)
                                                val articles = parseArticles(jsonObject)
                                                val workoutPlans = parseWorkoutPlans(jsonObject)

                                                mainHandler.post {
                                                    callback.onSuccess(tips, articles, workoutPlans)
                                                }
                                            } else {
                                                Log.e(
                                                    "GeminiService",
                                                    "JSON is missing required fields: $jsonObject"
                                                )
                                                mainHandler.post {
                                                    callback.onError("API response missing required data")
                                                }
                                            }
                                        } catch (e: JsonSyntaxException) {
                                            Log.e(
                                                "GeminiService",
                                                "Failed to parse extracted JSON: $jsonPart",
                                                e
                                            )
                                            mainHandler.post {
                                                callback.onError("Failed to parse JSON: ${e.message}")
                                            }
                                        }
                                    } else {
                                        Log.e(
                                            "GeminiService",
                                            "No JSON object found in response: $textResponse"
                                        )
                                        mainHandler.post {
                                            callback.onError("API didn't return valid JSON format")
                                        }
                                    }
                                } else {
                                    Log.e("GeminiService", "Response text is null or empty")
                                    mainHandler.post {
                                        callback.onError("Empty response from API")
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("GeminiService", "Error parsing response", e)
                                mainHandler.post {
                                    callback.onError("Failed to parse response: ${e.message}")
                                }
                            }
                        } else {
                            Log.e(
                                "GeminiService",
                                "API error: ${response.code()} - ${response.message()}"
                            )
                            Log.e("GeminiService", "Error body: ${response.errorBody()?.string()}")
                            mainHandler.post {
                                callback.onError("API error: ${response.code()} - ${response.message()}")
                            }
                        }
                    }

                    override fun onFailure(call: Call<GeminiResponse>, t: Throwable) {
                        Log.e("GeminiService", "Network error", t)
                        mainHandler.post {
                            callback.onError("Network error: ${t.message}")
                        }
                    }
                })
        }
    }

    private fun parseTips(jsonObject: JsonObject): List<Tip> {
        val tipsArray = jsonObject.getAsJsonArray("tips")
        return tipsArray.map { tipElement ->
            val tipObj = tipElement.asJsonObject
            Tip(
                id = UUID.randomUUID().toString(),
                title = tipObj.get("title").asString,
                content = tipObj.get("content").asString,
                createdAt = Date()
            )
        }
    }

    private fun parseArticles(jsonObject: JsonObject): List<Article> {
        val articlesArray = jsonObject.getAsJsonArray("articles")
        return articlesArray.map { articleElement ->
            val articleObj = articleElement.asJsonObject
            Article(
                id = UUID.randomUUID().toString(),
                title = articleObj.get("title").asString,
                content = articleObj.get("content").asString,
                imageUrl = articleObj.get("imageUrl").asString,
                category = articleObj.get("category").asString,
                createdAt = Date()
            )
        }
    }

    private fun parseWorkoutPlans(jsonObject: JsonObject): List<WorkoutPlan> {
        val workoutPlansArray = jsonObject.getAsJsonArray("workoutPlans")
        return workoutPlansArray.map { planElement ->
            val planObj = planElement.asJsonObject
            WorkoutPlan(
                id = UUID.randomUUID().toString(),
                title = planObj.get("title").asString,
                content = planObj.get("content").asString,
                difficulty = planObj.get("difficulty").asString,
                duration = planObj.get("duration").asString,
                targetMuscleGroup = planObj.get("targetMuscleGroup").asString,
                createdAt = Date()
            )
        }
    }
}