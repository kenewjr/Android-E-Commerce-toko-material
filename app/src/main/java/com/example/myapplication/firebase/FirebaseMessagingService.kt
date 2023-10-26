package com.example.myapplication.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.myapplication.R
import com.example.myapplication.view.buyer.NotifikasiBuyerActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class FirebaseMessagingService : FirebaseMessagingService() {

    private val ChannelId="abrar"

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val inte = Intent(this,NotifikasiBuyerActivity::class.java)
        inte.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE)
        createNotificationChannel(manager as NotificationManager)
        val intel = PendingIntent.getActivities(this,0, arrayOf(inte),PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this,ChannelId)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(R.drawable.ic_notifications)
            .setAutoCancel(true)
            .setContentIntent(intel)
            .build()

        manager.notify(Random.nextInt(),notification)
    }

    private fun createNotificationChannel(manager: NotificationManager){
        val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(ChannelId,"abrarchat",NotificationManager.IMPORTANCE_HIGH)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        channel.description = "new chat"
        channel.enableLights(true)
        manager.createNotificationChannel(channel)
    }
}