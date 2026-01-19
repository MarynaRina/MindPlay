package com.mind.play.core.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mind.play.data.datastore.settingsDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.datastore.preferences.core.booleanPreferencesKey

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val preferences = context.settingsDataStore.data.first()
                    val notificationsEnabled = preferences[booleanPreferencesKey("notifications")] ?: false

                    if (notificationsEnabled) {
                        val scheduler = NotificationScheduler(context)
                        scheduler.scheduleDailyReminder()
                    }
                } catch (e: Exception) {
                }
            }
        }
    }
}

