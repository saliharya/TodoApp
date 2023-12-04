package com.dicoding.todoapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationManagerCompat

class TodoListApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = NotificationManagerCompat.from(applicationContext)

            var notificationChannel =
                notificationManager.getNotificationChannel(getString(R.string.notify_channel_name))
            if (notificationChannel == null) {
                val importance = NotificationManager.IMPORTANCE_HIGH
                notificationChannel = NotificationChannel(
                    getString(R.string.notify_channel_name),
                    getString(R.string.notify_channel_name),
                    importance
                )
                notificationChannel.lightColor = Color.GREEN
                notificationChannel.enableVibration(true)
                notificationManager.createNotificationChannel(notificationChannel)
            }
        }
    }
}