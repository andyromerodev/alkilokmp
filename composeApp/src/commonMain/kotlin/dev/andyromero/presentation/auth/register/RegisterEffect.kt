package dev.andyromero.presentation.auth.register

internal sealed interface RegisterEffect {
    data class ShowError(val message: String) : RegisterEffect
    data class ShowSuccess(val message: String) : RegisterEffect
    data class NavigateToLogin(val returnPropertyId: String?) : RegisterEffect
    data class NavigateToBooking(val propertyId: String) : RegisterEffect
    data object NavigateToPropertyList : RegisterEffect
}
