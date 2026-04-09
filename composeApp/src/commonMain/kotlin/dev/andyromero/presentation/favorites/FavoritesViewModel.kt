package dev.andyromero.presentation.favorites

import dev.andyromero.core.result.Result
import dev.andyromero.domain.usecase.favorites.ObserveFavoritesUseCase
import dev.andyromero.domain.usecase.favorites.ToggleFavoriteUseCase
import dev.andyromero.domain.usecase.property.GetPropertiesUseCase
import dev.andyromero.presentation.base.BaseViewModel

internal class FavoritesViewModel(
    private val getPropertiesUseCase: GetPropertiesUseCase,
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : BaseViewModel<FavoritesEffect, FavoritesIntent, FavoritesState>(FavoritesState()) {

    init {
        observeFavorites()
        sendIntent(FavoritesIntent.Load)
    }

    override suspend fun handleIntent(intent: FavoritesIntent) {
        when (intent) {
            FavoritesIntent.Load -> load()
            is FavoritesIntent.ToggleFavorite -> toggleFavorite(intent.propertyId)
            is FavoritesIntent.OpenProperty -> emitEffect(FavoritesEffect.NavigateToPropertyDetail(intent.propertyId))
        }
    }

    private fun observeFavorites() {
        launch {
            observeFavoritesUseCase().collect { favorites ->
                setState { copy(favoriteIds = favorites) }
            }
        }
    }

    private fun load() {
        launch {
            setState { copy(isLoading = true, errorMessage = null) }
            when (val result = getPropertiesUseCase()) {
                is Result.Success -> {
                    setState {
                        copy(
                            isLoading = false,
                            properties = result.data,
                            errorMessage = null,
                        )
                    }
                }

                is Result.Error -> {
                    setState {
                        copy(
                            isLoading = false,
                            errorMessage = result.error.message,
                        )
                    }
                    emitEffect(FavoritesEffect.ShowError(result.error.message))
                }
            }
        }
    }

    private fun toggleFavorite(propertyId: String) {
        launch {
            when (val result = toggleFavoriteUseCase(propertyId)) {
                is Result.Success -> Unit
                is Result.Error -> emitEffect(FavoritesEffect.ShowError(result.error.message))
            }
        }
    }
}
