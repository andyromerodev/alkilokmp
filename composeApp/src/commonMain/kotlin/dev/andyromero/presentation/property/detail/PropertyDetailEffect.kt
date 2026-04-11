package dev.andyromero.presentation.property.detail

sealed interface PropertyDetailEffect {
    data class NavigateToBooking(val propertyId: String) : PropertyDetailEffect
    data object NavigateBack : PropertyDetailEffect
    data class ShowError(val message: String) : PropertyDetailEffect
}
