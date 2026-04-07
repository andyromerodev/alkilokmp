package dev.andyromero.di

import dev.andyromero.presentation.auth.login.LoginViewModel
import dev.andyromero.presentation.auth.register.RegisterViewModel
import org.koin.dsl.module

internal val presentationModule = module {
    factory { (returnPropertyId: String?) ->
        LoginViewModel(
            loginUseCase = get(),
            returnPropertyId = returnPropertyId,
        )
    }

    factory { (returnPropertyId: String?) ->
        RegisterViewModel(
            registerUseCase = get(),
            returnPropertyId = returnPropertyId,
        )
    }
}
