package com.example.mpgsandroidapp.api

import android.util.Base64
import androidx.annotation.StringRes
import com.example.mpgsandroidapp.R
import com.example.mpgsandroidapp.activities.App
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MiddlewareClient {
    //Keep if Basic Auth is required
    //private val AUTH = "Basic " + Base64.encodeToString(
    //    (Strings.get(R.string.AUTH_USERNAME) + ":" + Strings.get(R.string.AUTH_PASSWORD)).toByteArray(), Base64.NO_WRAP)

    private val baseUrl = Strings.get(R.string.MERCHANT_SERVER_URL)

    //TODO: Find out how to add a timeout limit
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()

            val requestBuilder = original.newBuilder()
                .addHeader("APIKEY", Strings.get(R.string.API_KEY))
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

object Strings {
    fun get(@StringRes stringRes: Int, vararg formatArgs: Any = emptyArray()): String {
        return App.instance.getString(stringRes, *formatArgs)
    }
}