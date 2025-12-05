package com.mind.play.core.di

import com.mind.play.data.repository.SettingsRepository
import org.koin.dsl.module

val appModule = module {
    single { SettingsRepository() }
}

val allModules = listOf(
    appModule
)
