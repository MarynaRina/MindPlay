package com.mind.play.core.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.mind.play.core.notifications.NotificationScheduler
import com.mind.play.core.sound.SoundManager
import com.mind.play.data.database.AppDatabase
import com.mind.play.data.datastore.settingsDataStore
import com.mind.play.data.repository.ProgressRepositoryImpl
import com.mind.play.data.repository.SettingsRepository
import com.mind.play.domain.repository.ProgressRepository
import com.mind.play.ui.dashboard.DashboardViewModel
import com.mind.play.ui.onboarding.OnboardingViewModel
import com.mind.play.ui.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<DataStore<Preferences>> { androidContext().settingsDataStore }
    
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "mindplay_database"
        ).fallbackToDestructiveMigration()
            .build()
    }
    
    single { get<AppDatabase>().progressDao() }
    single { get<AppDatabase>().gameResultDao() }
    
    single { SettingsRepository(get()) }
    single<ProgressRepository> { ProgressRepositoryImpl(get(), get()) }
    
    // Sound Manager (singleton)
    single { SoundManager(androidContext(), get()) }

    // Notification Scheduler
    single { NotificationScheduler(androidContext()) }

    viewModel { OnboardingViewModel(get()) }
    viewModel { DashboardViewModel(get()) }
    viewModel { SettingsViewModel(get<SettingsRepository>(), get<NotificationScheduler>()) }
}

val allModules = listOf(
    appModule
)
