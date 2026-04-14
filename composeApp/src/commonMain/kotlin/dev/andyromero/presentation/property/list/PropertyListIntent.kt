package dev.andyromero.presentation.property.list

import dev.andyromero.domain.model.PropertyType

sealed interface PropertyListIntent {
    data object LoadInitial : PropertyListIntent
    data object LoadNextPage : PropertyListIntent
    data object RetryLoad : PropertyListIntent
    data class SelectType(val type: PropertyType?) : PropertyListIntent
    data class UpdateSearchQuery(val query: String) : PropertyListIntent
    data class SaveScrollPosition(val index: Int, val offset: Int) : PropertyListIntent
    data class ToggleFavorite(val propertyId: String) : PropertyListIntent
    data class OpenProperty(val propertyId: String) : PropertyListIntent
}
