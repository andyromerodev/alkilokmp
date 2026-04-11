package dev.andyromero.di

import dev.andyromero.presentation.auth.login.LoginViewModel
import dev.andyromero.presentation.auth.register.RegisterViewModel
import dev.andyromero.presentation.favorites.FavoritesViewModel
import dev.andyromero.presentation.property.detail.PropertyDetailViewModel
import dev.andyromero.presentation.property.list.PropertyListViewModel
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

    factory {
        PropertyListViewModel(
            getPropertiesUseCase = get(),
            observeFavoritesUseCase = get(),
            toggleFavoriteUseCase = get(),
        )
    }

    factory {
        FavoritesViewModel(
            getPropertiesUseCase = get(),
            observeFavoritesUseCase = get(),
            toggleFavoriteUseCase = get(),
        )
    }

    factory { (propertyId: String) ->
        PropertyDetailViewModel(
            propertyId = propertyId,
            getPropertyByIdUseCase = get(),
        )
    }
}
