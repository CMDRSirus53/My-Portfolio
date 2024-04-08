package com.csu_itc303_team1.adhdtaskmanager.utils.alarm_manager

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.csu_itc303_team1.adhdtaskmanager.MainActivity
import com.csu_itc303_team1.adhdtaskmanager.R

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val title = intent?.getStringExtra("EXTRA_TITLE") ?: return
        val description = intent.getStringExtra("EXTRA_DESCRIPTION") ?: return
        val itemId = intent.getIntExtra("EXTRA_ID", 0)

        showNotification(context!!, itemId, title, description)
    }

    private fun showNotification(context: Context, id: Int, title: String, description: String) {
        val notificationManager = context.getSystemService(ComponentActivity.NOTIFICATION_SERVICE) as NotificationManager
        val resultIntent = Intent(context, MainActivity::class.java)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val notification = NotificationCompat.Builder(
            context,
            "TodoNotification"
        )
            .setContentTitle(title)
            .setContentText(description)
            .setAutoCancel(true)
            .setContentIntent(resultPendingIntent)
            .setSmallIcon(R.drawable.new_logo)
            .build()
        notificationManager.notify(id, notification)
    }
}