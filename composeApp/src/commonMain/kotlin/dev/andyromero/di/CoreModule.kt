package dev.andyromero.di

import dev.andyromero.core.dispatcher.DefaultDispatcherProvider
import dev.andyromero.core.dispatcher.DispatcherProvider
import dev.andyromero.core.logging.Logger
import dev.andyromero.core.platform.NetworkStatusProvider
import dev.andyromero.core.platform.PlatformConfigProvider
import dev.andyromero.core.platform.PlatformLogger
import org.koin.dsl.module

internal val coreModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }
    single { NetworkStatusProvider() }
    single { PlatformConfigProvider() }
    single<Logger> { PlatformLogger() }
}
