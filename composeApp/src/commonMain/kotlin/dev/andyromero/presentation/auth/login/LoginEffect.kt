package dev.andyromero.presentation.auth.login

internal sealed interface LoginEffect {
    data class ShowError(val message: String) : LoginEffect
    data class NavigateToRegister(val returnPropertyId: String?) : LoginEffect
    data class NavigateToBooking(val propertyId: String) : LoginEffect
    data object NavigateToPropertyList : LoginEffect
}
