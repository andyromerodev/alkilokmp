package dev.andyromero.presentation.property.list

import dev.andyromero.core.result.Result
import dev.andyromero.domain.model.PropertyType
import dev.andyromero.domain.usecase.favorites.ObserveFavoritesUseCase
import dev.andyromero.domain.usecase.favorites.ToggleFavoriteUseCase
import dev.andyromero.domain.usecase.property.GetPropertiesUseCase
import dev.andyromero.presentation.base.BaseViewModel

internal class PropertyListViewModel(
    private val getPropertiesUseCase: GetPropertiesUseCase,
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : BaseViewModel<PropertyListEffect, PropertyListIntent, PropertyListState>(PropertyListState()) {
    private companion object {
        const val PAGE_SIZE = 3
    }

    init {
        observeFavorites()
        sendIntent(PropertyListIntent.LoadInitial)
    }

    override suspend fun handleIntent(intent: PropertyListIntent) {
        when (intent) {
            PropertyListIntent.LoadInitial -> load(reset = true)
            PropertyListIntent.LoadNextPage -> load(reset = false)
            is PropertyListIntent.SelectType -> selectType(intent.type)
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

    private fun load(reset: Boolean) {
        launch {
            val current = currentState
            if (reset.not() && (current.isLoading || current.isPaging || current.canLoadMore.not())) return@launch

            if (reset) {
                setState {
                    copy(
                        isLoading = true,
                        isPaging = false,
                        canLoadMore = true,
                        nextPage = 0,
                        properties = emptyList(),
                        errorMessage = null,
                    )
                }
            } else {
                setState { copy(isPaging = true, errorMessage = null) }
            }

            val pageToLoad = if (reset) 0 else current.nextPage
            when (
                val result = getPropertiesUseCase(
                    page = pageToLoad,
                    pageSize = PAGE_SIZE,
                    type = currentState.selectedType,
                )
            ) {
                is Result.Success -> {
                    val loaded = result.data
                    val hasMore = loaded.size >= PAGE_SIZE
                    setState {
                        val merged = if (reset) {
                            loaded
                        } else {
                            (properties + loaded).distinctBy { it.id }
                        }
                        copy(
                            isLoading = false,
                            isPaging = false,
                            properties = merged,
                            canLoadMore = hasMore,
                            nextPage = if (hasMore) pageToLoad + 1 else pageToLoad,
                            errorMessage = null,
                        )
                    }
                }

                is Result.Error -> {
                    setState {
                        copy(
                            isLoading = false,
                            isPaging = false,
                            errorMessage = result.error.message,
                        )
                    }
                    emitEffect(PropertyListEffect.ShowError(result.error.message))
                }
            }
        }
    }

    private fun selectType(type: PropertyType?) {
        setState {
            copy(
                selectedType = type,
            )
        }
        load(reset = true)
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
