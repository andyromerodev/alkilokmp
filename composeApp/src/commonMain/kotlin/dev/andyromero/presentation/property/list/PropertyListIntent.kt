package dev.andyromero.presentation.property.list

sealed interface PropertyListIntent {
    data object Load : PropertyListIntent
    data class ToggleFavorite(val propertyId: String) : PropertyListIntent
    data class OpenProperty(val propertyId: String) : PropertyListIntent
}
