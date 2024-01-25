package com.example.myapplication.di

import android.util.Log
import com.example.myapplication.network.ApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val BASE_URL = "http://abrar.vzcyberd.my.id/API/"
    private  val logging : HttpLoggingInterceptor
        get(){
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            return httpLoggingInterceptor.apply {
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            }
        }

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)
            if (response.code == 404) {
                // Lakukan penanganan khusus untuk respons 404 di sini
                // Misalnya, tangani respons 404 dengan menampilkan pesan kesalahan atau melakukan tindakan tertentu.
                val responseBody = response.body
                val errorMessage = response.body?.string() ?: "Error 404: Not Found"
                Log.e("Error 404", errorMessage)
                responseBody?.close()
            }
            response
        }
        .addInterceptor(logging)
        .build()

    private var gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    @Singleton
    @Provides
    fun provideRetrofit() : Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()

    @Singleton
    @Provides
    fun provideApi(retrofit: Retrofit) : ApiService =
        retrofit.create(ApiService::class.java)
}