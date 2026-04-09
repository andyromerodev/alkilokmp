package dev.andyromero.presentation.property.list

import dev.andyromero.core.result.Result
import dev.andyromero.domain.usecase.favorites.ObserveFavoritesUseCase
import dev.andyromero.domain.usecase.favorites.ToggleFavoriteUseCase
import dev.andyromero.domain.usecase.property.GetPropertiesUseCase
import dev.andyromero.presentation.base.BaseViewModel

internal class PropertyListViewModel(
    private val getPropertiesUseCase: GetPropertiesUseCase,
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : BaseViewModel<PropertyListEffect, PropertyListIntent, PropertyListState>(PropertyListState()) {

    init {
        observeFavorites()
        sendIntent(PropertyListIntent.Load)
    }

    override suspend fun handleIntent(intent: PropertyListIntent) {
        when (intent) {
            PropertyListIntent.Load -> load()
            is PropertyListIntent.ToggleFavorite -> toggleFavorite(intent.propertyId)
            is PropertyListIntent.OpenProperty -> emitEffect(PropertyListEffect.NavigateToPropertyDetail(intent.propertyId))
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
                    emitEffect(PropertyListEffect.ShowError(result.error.message))
                }
            }
        }
    }

    private fun toggleFavorite(propertyId: String) {
        launch {
            when (val result = toggleFavoriteUseCase(propertyId)) {
                is Result.Success -> Unit
                is Result.Error -> emitEffect(PropertyListEffect.ShowError(result.error.message))
            }
        }
    }
}
