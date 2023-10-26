package com.example.myapplication.network

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.myapplication.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyRetrofitService : Service() {
    private lateinit var apiClient: ApiClient
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Tempatkan kode Retrofit di sini
        apiClient.getApiService().getmidtransNotif().enqueue(object : Callback<com.example.myapplication.model.Response>{
            override fun onResponse(
                call: Call<com.example.myapplication.model.Response>,
                response: Response<com.example.myapplication.model.Response>
            ) {
                if (response.isSuccessful) {
                    sendNotification(this@MyRetrofitService,"Ada Pesanan Datang","ini")
                }else{
                    Log.e("notif",response.body().toString())
                }
            }

            override fun onFailure(call: Call<com.example.myapplication.model.Response>, t: Throwable) {
                Log.e("notif",t.message.toString())
            }

        })
        return START_STICKY
    }

    fun sendNotification(context: Context, title: String, content: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "your_channel_id",
                "Your Channel Name",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(context, "your_channel_id")
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationId = 1 // You can use a unique ID for each notification
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}