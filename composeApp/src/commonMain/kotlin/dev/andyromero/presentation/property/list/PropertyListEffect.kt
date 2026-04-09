package dev.andyromero.presentation.property.list

sealed interface PropertyListEffect {
    data class ShowError(val message: String) : PropertyListEffect
    data class NavigateToPropertyDetail(val propertyId: String) : PropertyListEffect
}
