package dev.andyromero.presentation.booking.create

import dev.andyromero.domain.model.AvailabilityDay
import dev.andyromero.presentation.booking.components.nightsBetween

internal data class CreateBookingState(
    val isLoading: Boolean = true,
    val propertyTitle: String = "",
    val pricePerNight: Double = 0.0,
    val availability: List<AvailabilityDay> = emptyList(),
    val selectedCheckIn: Long? = null,
    val selectedCheckOut: Long? = null,
    val isSelectedRangeValid: Boolean = false,
    val hostPhone: String? = null,
    val manualPhone: String = "",
    val showManualPhoneDialog: Boolean = false,
    val error: String? = null,
) {
    val isRangeComplete: Boolean
        get() = selectedCheckIn != null && selectedCheckOut != null

    val selectedNights: Int
        get() = nightsBetween(selectedCheckIn, selectedCheckOut)

    val isSendEnabled: Boolean
        get() = !isLoading && isRangeComplete && isSelectedRangeValid
}
