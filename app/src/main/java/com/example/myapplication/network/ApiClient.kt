package com.example.myapplication.network

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Suppress("PropertyName", "PropertyName", "PrivatePropertyName")
class ApiClient {
    private lateinit var apiService: ApiService

    private val BASE = "http://abrar.vzcyberd.my.id/API/"

    private  val logging : HttpLoggingInterceptor
        get() {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            return httpLoggingInterceptor.apply {
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            }
        }

    private var gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)
            if (response.code == 404) {
                // Lakukan penanganan khusus untuk respons 404 di sini
                val responseBody = response.body
                val errorMessage = response.body?.string() ?: "Error 404: Not Found"
                Log.e("Error 404", errorMessage)
                responseBody?.close()
                // Misalnya, tangani respons 404 dengan menampilkan pesan kesalahan atau melakukan tindakan tertentu.
            }
            response
        }
        .addInterceptor(logging)
        .build()
    fun getApiService(): ApiService {

        // Initialize ApiService if not initialized yet
        if (!::apiService.isInitialized) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()

            apiService = retrofit.create(ApiService::class.java)
        }

        return apiService
    }
}