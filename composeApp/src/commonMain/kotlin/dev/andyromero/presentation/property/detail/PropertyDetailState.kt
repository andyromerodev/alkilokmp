package dev.andyromero.presentation.property.detail

import dev.andyromero.domain.model.Property

data class PropertyDetailState(
    val isLoading: Boolean = false,
    val property: Property? = null,
    val errorMessage: String? = null,
)
