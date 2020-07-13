package com.example.mpgsandroidapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mpgsandroidapp.R
import com.google.gson.GsonBuilder
import com.mastercard.gateway.android.sdk.Gateway
import com.mastercard.gateway.android.sdk.GatewayCallback
import com.mastercard.gateway.android.sdk.GatewayMap
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.mpgsandroidapp.api.MiddlewareClient
import com.example.mpgsandroidapp.models.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialise all MPGS variables
        val gateway = Gateway().setMerchantId(getString(R.string.MID)).setRegion(Gateway.Region.MTF)
        var callback: GatewayCallback = object : GatewayCallback {
            override fun onSuccess(response: GatewayMap) {
                tvUpdate.setText(tvUpdate.getText().toString() + "\n---Session Update Successful---")
            }

            override fun onError(throwable: Throwable) {
                tvUpdate.setText(tvUpdate.getText().toString() + "\n---ERROR: Session Update FAILED---")
            }
        }
        val paymentInfo = SourceOfFunds(
            "CARD",
            Provided(
                Card(
                    "A Person",
                    "FULL_PAN",
                    Expiry(
                        "MM",
                        "YY"
                    ),
                    "CVV")
            )
        )
        val apiVersion = "52"
        val request = GatewayMap()
            .set("sourceOfFunds.provided.card.nameOnCard", paymentInfo.provided.card.nameOnCard)
            .set("sourceOfFunds.provided.card.number", paymentInfo.provided.card.number)
            .set("sourceOfFunds.provided.card.securityCode", paymentInfo.provided.card.securityCode)
            .set("sourceOfFunds.provided.card.expiry.month", paymentInfo.provided.card.expiry.month)
            .set("sourceOfFunds.provided.card.expiry.year", paymentInfo.provided.card.expiry.year)

        var sessionId = "NONE"

        val gsonPretty = GsonBuilder().setPrettyPrinting().create()

        // Get Session Button Click
        cmdSession.setOnClickListener{
            // Ask Middleware Server for a Session
            MiddlewareClient.instance.startPayment()
                .enqueue(object: Callback<GetSessionResponse>{
                    override fun onFailure(call: Call<GetSessionResponse>, t: Throwable) {
                        tvMain.setText(getString(R.string.apiDown))
                    }
                    override fun onResponse(
                        call: Call<GetSessionResponse>,
                        response: Response<GetSessionResponse>
                    ) {
                        tvMain.setText(gsonPretty.toJson(response.body()))
                        sessionId = response.body()?.id.toString()
                    }
                })
        }

        // Update Button Click
        cmdUpdate.setOnClickListener{
            tvUpdate.setText("---Sending---\n" + gsonPretty.toJson(paymentInfo))

            if (sessionId != "NONE") {
                // Update the Session
                gateway.updateSession(sessionId, apiVersion, request, callback)

            } else {
                tvUpdate.setText(getString(R.string.defaultSession))
            }
        }

        // Pay Button Click
        cmdPay.setOnClickListener{
            // Send the PAY Request
            MiddlewareClient.instance.finishPayment(gsonPretty.toJson(sessionId))
                .enqueue(object: Callback<GetPaymentResponse>{
                    override fun onFailure(call: Call<GetPaymentResponse>, t: Throwable) {
                        tvPay.setText("Error with Request:\n" + call.request().toString())
                    }
                    override fun onResponse(
                        call: Call<GetPaymentResponse>,
                        response: Response<GetPaymentResponse>
                    ) {
                        tvPay.setText(gsonPretty.toJson(response.body()))
                    }
                })
        }
    }
}
