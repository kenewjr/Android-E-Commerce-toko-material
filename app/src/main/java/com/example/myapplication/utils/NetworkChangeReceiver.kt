@file:Suppress("DEPRECATION")

package com.example.myapplication.utils

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import com.example.myapplication.model.Response
import com.example.myapplication.network.ApiClient
import retrofit2.Call
import retrofit2.Callback

class NetworkChangeReceiver : BroadcastReceiver() {
    private lateinit var apiClient: ApiClient
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        if (isNetworkConnected(context)) {
            apiClient.getApiService().getmidtransNotif().enqueue(object : Callback<Response> {
                override fun onResponse(
                    call: Call<Response>,
                    response: retrofit2.Response<Response>
                ) {
                    if (response.isSuccessful) {
//                        val data = response.body()
                    } else {
                        Log.e("notif", response.body().toString())
                    }
                }

                override fun onFailure(call: Call<Response>, t: Throwable) {
                    Log.e("notif", t.message.toString())
                }

            })
        }
    }
    private fun isNetworkConnected(context: Context?): Boolean {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}
