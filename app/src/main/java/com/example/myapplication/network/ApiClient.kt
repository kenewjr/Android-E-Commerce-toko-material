package com.example.myapplication.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {
    private lateinit var apiService: ApiService
    private lateinit var apiMidtrans: ApiMidtrans
    val BASE = "http://192.168.1.150/skripsi/"


    private  val logging : HttpLoggingInterceptor
        get() {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            return httpLoggingInterceptor.apply {
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            }
        }

    var gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    private val clint = OkHttpClient.Builder().addInterceptor(logging).build()

//    val apiMidtrans = Retrofit.Builder()
//        .baseUrl("https://api.sandbox.midtrans.com/v2/")
//        .addConverterFactory(GsonConverterFactory.create())
//        .client(clint)
//        .build()

    fun getApiMidtrans(): ApiMidtrans {

        // Initialize ApiService if not initialized yet
        if (!::apiService.isInitialized) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(clint)
                .build()

            apiService = retrofit.create(ApiService::class.java)
        }

        return apiMidtrans
    }

    fun getApiService(): ApiService {

        // Initialize ApiService if not initialized yet
        if (!::apiService.isInitialized) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(clint)
                .build()

            apiService = retrofit.create(ApiService::class.java)
        }

        return apiService
    }
}