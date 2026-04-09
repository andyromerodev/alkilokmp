package dev.andyromero.presentation.property.list

import dev.andyromero.domain.model.PropertyType

sealed interface PropertyListIntent {
    data object LoadInitial : PropertyListIntent
    data object LoadNextPage : PropertyListIntent
    data class SelectType(val type: PropertyType?) : PropertyListIntent
    data class ToggleFavorite(val propertyId: String) : PropertyListIntent
    data class OpenProperty(val propertyId: String) : PropertyListIntent
}
