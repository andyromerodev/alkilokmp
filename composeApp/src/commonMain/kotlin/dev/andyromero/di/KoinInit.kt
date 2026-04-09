package dev.andyromero.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.mp.KoinPlatform

internal expect fun platformModule(): Module

internal fun initKoinIfNeeded(appDeclaration: KoinAppDeclaration = {}) {
    if (KoinPlatform.getKoinOrNull() != null) {
        println("🔵 [Koin] already running — skip")
        return
    }
    println("🔵 [Koin] startKoin — begin")
    startKoin {
        appDeclaration()
        modules(appModules + platformModule())
    }
    println("🔵 [Koin] startKoin — done")
}
