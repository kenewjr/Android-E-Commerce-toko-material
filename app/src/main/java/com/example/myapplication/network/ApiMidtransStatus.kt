package com.example.myapplication.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiMidtransStatus {
    companion object {
        val SANDBOX = "https://api.sandbox.midtrans.com/v2/"
        fun getRetrofitInstance(): Retrofit {
            val logging = HttpLoggingInterceptor()
            logging.level = (HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
            client.addInterceptor(logging)

            return Retrofit.Builder().baseUrl(SANDBOX).client(client.build())
                .addConverterFactory(GsonConverterFactory.create()).build()
        }
    }
}