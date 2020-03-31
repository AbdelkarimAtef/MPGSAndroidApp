package com.example.mpgsandroidapp.api

import android.util.Base64
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MiddlewareClient {
    //Keep is Basic Auth is required
    private val AUTH = "Basic " + Base64.encodeToString("MERCHANT_SERVER_USERNAME:MERCHANT_SERVER_PASSWORD".toByteArray(), Base64.NO_WRAP)

    private const val baseUrl = "http://MERCHANT_SERVER_URL/"

    //TODO: Find out how to add a timeout limit
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()

            val requestBuilder = original.newBuilder()
                .addHeader("Authorization",
                    AUTH
                )
                .method(original.method(), original.body())

            val request = requestBuilder.build()
            chain.proceed(request)
        }.build()

    val instance: MiddlewareApi by lazy{
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        retrofit.create(MiddlewareApi::class.java)
    }
}