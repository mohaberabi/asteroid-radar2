package com.example.nasa.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.nasa.MainActivity
import com.example.nasa.R

import java.io.Serializable

object NotiConst {
    const val NOTIFICATION_ID = 0
    const val NOTIFICATION_CHANNEL = "asteroidRadder"

}

fun NotificationManager.showNoti(
    body: String,
    title: String,

    appContext: Context,
) {

    if (!areNotificationsEnabled()) {
        return
    }

    val builder = NotificationCompat.Builder(appContext, NotiConst.NOTIFICATION_CHANNEL)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(title)
        .setContentText(if (body.isEmpty()) "" else body)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)


    notify(NotiConst.NOTIFICATION_ID, builder.build())


}