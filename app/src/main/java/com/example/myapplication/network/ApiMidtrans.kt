package com.example.myapplication.network

import com.example.myapplication.payment.GetPaymentStatus
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiMidtrans {
    @GET("{ORDER_ID}/status")
    fun getStatus(
        @Path("ORDER_ID") ORDER_ID:String
    ): Call<GetPaymentStatus>

}