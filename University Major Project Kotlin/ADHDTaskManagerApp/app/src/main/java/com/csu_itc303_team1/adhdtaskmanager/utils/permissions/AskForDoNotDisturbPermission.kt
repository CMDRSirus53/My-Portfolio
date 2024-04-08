package com.csu_itc303_team1.adhdtaskmanager.utils.permissions

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.provider.Settings


fun getDoNotDisturbPermission(activity: Activity) {
    val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
    activity.startActivity(intent)
}

fun toggleDoNotDisturb(
    context: Context,
    activity: Activity) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (!notificationManager.isNotificationPolicyAccessGranted) {
        getDoNotDisturbPermission(activity)
    } else {
        if (notificationManager.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_NONE) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
        } else {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
        }
    }
}

// check if do not disturb is enabled
fun isDoNotDisturbEnabled(context: Context): Boolean {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    return notificationManager.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_NONE
}