package dev.andyromero.presentation.favorites

import dev.andyromero.core.result.Result
import dev.andyromero.domain.usecase.favorites.GetFavoritePropertiesUseCase
import dev.andyromero.domain.usecase.favorites.ObserveFavoritesUseCase
import dev.andyromero.domain.usecase.favorites.ToggleFavoriteUseCase
import dev.andyromero.presentation.base.BaseViewModel

internal class FavoritesViewModel(
    private val getFavoritePropertiesUseCase: GetFavoritePropertiesUseCase,
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : BaseViewModel<FavoritesEffect, FavoritesIntent, FavoritesState>(FavoritesState()) {

    init {
        observeAndLoad()
    }

    override suspend fun handleIntent(intent: FavoritesIntent) {
        when (intent) {
            is FavoritesIntent.ToggleFavorite -> toggleFavorite(intent.propertyId)
            is FavoritesIntent.OpenProperty -> emitEffect(FavoritesEffect.NavigateToPropertyDetail(intent.propertyId))
        }
    }

    private fun observeAndLoad() {
        launch {
            observeFavoritesUseCase().collect { favoriteIds ->
                setState { copy(favoriteIds = favoriteIds, isLoading = true, errorMessage = null) }
                when (val result = getFavoritePropertiesUseCase()) {
                    is Result.Success -> setState {
                        copy(isLoading = false, properties = result.data)
                    }
                    is Result.Error -> {
                        setState { copy(isLoading = false, errorMessage = result.error.message) }
                        emitEffect(FavoritesEffect.ShowError(result.error.message))
                    }
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
