package dev.andyromero.presentation.booking.create

internal sealed interface CreateBookingEffect {
    data class ShowError(val message: String) : CreateBookingEffect
    data class OpenWhatsApp(val url: String) : CreateBookingEffect
    data object NavigateBack : CreateBookingEffect
}
