package dev.andyromero.presentation.property.list

import dev.andyromero.domain.model.Property

data class PropertyListState(
    val isLoading: Boolean = false,
    val properties: List<Property> = emptyList(),
    val favoriteIds: Set<String> = emptySet(),
    val errorMessage: String? = null,
)
