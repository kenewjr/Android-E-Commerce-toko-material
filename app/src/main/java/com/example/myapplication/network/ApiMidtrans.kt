package com.example.myapplication.network

import com.example.myapplication.payment.GetPaymentStatus
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface ApiMidtrans {
    @GET("{ORDER_ID}/status")
    @Headers("Accept:application/json",
        "Content-Type:application/json",
        "x-api-key:SB-Mid-client-UyV8fwVUJHmHywYZ",
        "Authorization:Basic U0ItTWlkLXNlcnZlci1LYlAxYjMzU05JbzhlNEVvQ0xQM3l6NDM6")
    fun getStatus(
        @Path("ORDER_ID") ORDER_ID:String
    ): Call<GetPaymentStatus>

}