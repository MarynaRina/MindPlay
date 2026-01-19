package com.mind.play.core.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * BroadcastReceiver для отримання щоденних нагадувань
 *
 * Викликається AlarmManager в запланований час
 */
class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationScheduler = NotificationScheduler(context)
        notificationScheduler.showNotification()
    }
}

