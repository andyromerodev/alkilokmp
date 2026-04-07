package dev.andyromero.di

import dev.andyromero.domain.usecase.auth.GetCurrentUserUseCase
import dev.andyromero.domain.usecase.auth.LoginUseCase
import dev.andyromero.domain.usecase.auth.LogoutUseCase
import dev.andyromero.domain.usecase.auth.ObserveAuthStateUseCase
import dev.andyromero.domain.usecase.auth.ObserveCurrentProfileUseCase
import dev.andyromero.domain.usecase.auth.RegisterUseCase
import dev.andyromero.domain.usecase.auth.RestoreSessionUseCase
import dev.andyromero.domain.usecase.settings.ObserveThemeModeUseCase
import dev.andyromero.domain.usecase.settings.UpdateThemeModeUseCase
import org.koin.dsl.module

internal val domainModule = module {
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }
    factory { ObserveAuthStateUseCase(get()) }
    factory { ObserveCurrentProfileUseCase(get()) }
    factory { RestoreSessionUseCase(get()) }

    factory { ObserveThemeModeUseCase(get()) }
    factory { UpdateThemeModeUseCase(get()) }
}
