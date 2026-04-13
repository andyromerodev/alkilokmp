package dev.andyromero.presentation.booking.create

internal sealed interface CreateBookingIntent {
    data object Load : CreateBookingIntent
    data object Retry : CreateBookingIntent
    data class SelectDate(val dateMillisUtc: Long) : CreateBookingIntent
    data object ClearSelection : CreateBookingIntent
    data object SendWhatsAppClicked : CreateBookingIntent
    data class ManualPhoneChanged(val value: String) : CreateBookingIntent
    data object ConfirmManualPhone : CreateBookingIntent
    data object DismissManualPhoneDialog : CreateBookingIntent
    data object NavigateBack : CreateBookingIntent
}
