package com.example.mpgsandroidapp.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import com.example.mpgsandroidapp.models.GetPaymentResponse
import com.example.mpgsandroidapp.models.GetSessionResponse

interface MiddlewareApi {

    @POST("startpayment")
    fun startPayment(): Call<GetSessionResponse>

    @PUT("finishpayment")
    fun finishPayment(
        @Body id: String
    ): Call<GetPaymentResponse>

}