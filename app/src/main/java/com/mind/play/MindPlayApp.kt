package com.mind.play

import android.app.Application
import com.mind.play.core.di.allModules
import com.mind.play.core.notifications.NotificationScheduler
import com.mind.play.core.sound.SoundManager
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MindPlayApp : Application() {

    val soundManager: SoundManager by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@MindPlayApp)
            modules(allModules)
        }

        initNotificationChannel()
    }

    private fun initNotificationChannel() {
        val notificationScheduler = NotificationScheduler(this)
        notificationScheduler.createNotificationChannel()
    }

    override fun onTerminate() {
        super.onTerminate()
        soundManager.release()
    }
}
