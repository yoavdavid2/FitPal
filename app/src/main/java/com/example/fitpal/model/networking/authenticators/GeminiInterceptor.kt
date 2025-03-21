package com.example.fitpal.model.networking.authenticators

import com.example.fitpal.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class GeminiInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .url(
                chain.request().url.newBuilder()
                    .addQueryParameter("key", BuildConfig.GEMINI_API_KEY)
                    .build()
            ).build()

        return chain.proceed(request)
    }
}