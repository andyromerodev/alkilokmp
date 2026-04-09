package dev.andyromero.presentation.favorites

import dev.andyromero.domain.model.Property

data class FavoritesState(
    val isLoading: Boolean = false,
    val properties: List<Property> = emptyList(),
    val favoriteIds: Set<String> = emptySet(),
    val errorMessage: String? = null,
)
