package dev.andyromero.presentation.booking.create

import dev.andyromero.core.result.Result
import dev.andyromero.domain.usecase.auth.GetProfileUseCase
import dev.andyromero.domain.usecase.property.GetPropertyAvailabilityUseCase
import dev.andyromero.domain.usecase.property.GetPropertyByIdUseCase
import dev.andyromero.presentation.base.BaseViewModel
import dev.andyromero.presentation.booking.components.DAY_IN_MILLIS
import dev.andyromero.presentation.booking.components.formatDisplayDate
import dev.andyromero.presentation.booking.components.isRangeAvailable
import dev.andyromero.presentation.booking.components.normalizeUtcDate
import dev.andyromero.presentation.booking.components.toIsoDate
import dev.andyromero.presentation.booking.components.todayUtcMillis
import dev.andyromero.presentation.booking.components.utcMillisToSimpleDate

internal class CreateBookingViewModel(
    private val propertyId: String,
    private val getPropertyByIdUseCase: GetPropertyByIdUseCase,
    private val getPropertyAvailabilityUseCase: GetPropertyAvailabilityUseCase,
    private val getProfileUseCase: GetProfileUseCase,
) : BaseViewModel<CreateBookingEffect, CreateBookingIntent, CreateBookingState>(CreateBookingState()) {

    init {
        sendIntent(CreateBookingIntent.Load)
    }

    override suspend fun handleIntent(intent: CreateBookingIntent) {
        when (intent) {
            CreateBookingIntent.Load -> loadData()
            CreateBookingIntent.Retry -> loadData()
            is CreateBookingIntent.SelectDate -> selectDate(normalizeUtcDate(intent.dateMillisUtc))
            CreateBookingIntent.ClearSelection -> clearSelection()
            CreateBookingIntent.SendWhatsAppClicked -> sendWhatsApp()
            is CreateBookingIntent.ManualPhoneChanged -> setState { copy(manualPhone = intent.value) }
            CreateBookingIntent.ConfirmManualPhone -> confirmManualPhone()
            CreateBookingIntent.DismissManualPhoneDialog -> setState { copy(showManualPhoneDialog = false) }
            CreateBookingIntent.NavigateBack -> emitEffect(CreateBookingEffect.NavigateBack)
        }
    }

    private suspend fun loadData() {
        setState { copy(isLoading = true, error = null) }

        val property = when (val result = getPropertyByIdUseCase(propertyId)) {
            is Result.Success -> result.data
            is Result.Error -> {
                setState { copy(isLoading = false, error = result.error.message) }
                emitEffect(CreateBookingEffect.ShowError(result.error.message))
                return
            }
        }

        val todayUtc = todayUtcMillis()
        val endUtc = todayUtc + DAY_IN_MILLIS * 180
        val availability = when (val result = getPropertyAvailabilityUseCase(
            propertyId = property.id,
            startDate = toIsoDate(todayUtc),
            endDate = toIsoDate(endUtc),
        )) {
            is Result.Success -> result.data
            is Result.Error -> {
                setState { copy(isLoading = false, error = result.error.message) }
                emitEffect(CreateBookingEffect.ShowError(result.error.message))
                return
            }
        }

        val hostPhone = when (val result = getProfileUseCase(property.hostId)) {
            is Result.Success -> result.data.phone
            is Result.Error -> null
        }

        setState {
            copy(
                isLoading = false,
                propertyTitle = property.title,
                pricePerNight = property.pricePerNight,
                availability = availability,
                hostPhone = hostPhone,
                error = null,
            )
        }
    }

    private suspend fun selectDate(dateMillisUtc: Long) {
        val checkIn = currentState.selectedCheckIn
        val checkOut = currentState.selectedCheckOut

        if (checkIn == null || checkOut != null) {
            setState {
                copy(
                    selectedCheckIn = dateMillisUtc,
                    selectedCheckOut = null,
                    isSelectedRangeValid = false,
                    error = null,
                )
            }
            return
        }

        if (dateMillisUtc <= checkIn) {
            setState {
                copy(
                    selectedCheckIn = dateMillisUtc,
                    selectedCheckOut = null,
                    isSelectedRangeValid = false,
                    error = null,
                )
            }
            return
        }

        val availabilityByDate = currentState.availability.associate { it.date to it.isAvailable }
        if (!isRangeAvailable(checkIn, dateMillisUtc, availabilityByDate)) {
            setState { copy(selectedCheckOut = null, isSelectedRangeValid = false) }
            emitEffect(CreateBookingEffect.ShowError("Hay fechas no disponibles en el rango"))
            return
        }

        setState {
            copy(
                selectedCheckOut = dateMillisUtc,
                isSelectedRangeValid = true,
                error = null,
            )
        }
    }

    private fun clearSelection() {
        setState {
            copy(selectedCheckIn = null, selectedCheckOut = null, isSelectedRangeValid = false)
        }
    }

    private suspend fun sendWhatsApp() {
        if (!currentState.isRangeComplete || !currentState.isSelectedRangeValid) {
            emitEffect(CreateBookingEffect.ShowError("Selecciona un rango válido"))
            return
        }

        val phone = sanitizePhone(currentState.hostPhone)
        if (phone == null) {
            setState { copy(showManualPhoneDialog = true) }
            return
        }

        openWhatsApp(phone)
    }

    private suspend fun confirmManualPhone() {
        val phone = sanitizePhone(currentState.manualPhone)
        if (phone == null) {
            emitEffect(CreateBookingEffect.ShowError("Ingresa un número válido (8 a 15 dígitos)"))
            return
        }
        setState { copy(showManualPhoneDialog = false) }
        openWhatsApp(phone)
    }

    private suspend fun openWhatsApp(phoneDigits: String) {
        val checkIn = currentState.selectedCheckIn ?: return
        val checkOut = currentState.selectedCheckOut ?: return
        val nights = currentState.selectedNights
        if (nights <= 0) return

        val message = buildWhatsAppMessage(
            propertyTitle = currentState.propertyTitle,
            checkInDisplay = formatDisplayDate(checkIn),
            checkOutDisplay = formatDisplayDate(checkOut),
            nights = nights,
        )
        val encodedMessage = encodeForUrl(message)
        emitEffect(CreateBookingEffect.OpenWhatsApp("https://wa.me/$phoneDigits?text=$encodedMessage"))
    }

    private fun buildWhatsAppMessage(
        propertyTitle: String,
        checkInDisplay: String,
        checkOutDisplay: String,
        nights: Int,
    ): String {
        return "Hola, me interesa \"$propertyTitle\".\n" +
            "Quisiera consultar disponibilidad del $checkInDisplay al $checkOutDisplay ($nights noches).\n" +
            "¿Podemos coordinar por aquí?"
    }

    private fun sanitizePhone(raw: String?): String? {
        val value = raw?.trim().orEmpty()
        if (value.isBlank()) return null
        val digits = value.filter { it.isDigit() }
        if (digits.length !in 8..15) return null
        return digits
    }

    private fun encodeForUrl(text: String): String {
        val sb = StringBuilder()
        for (byte in text.encodeToByteArray()) {
            val ch = byte.toInt() and 0xFF
            if (ch in 0x41..0x5A || ch in 0x61..0x7A || ch in 0x30..0x39 ||
                ch == 0x2D || ch == 0x5F || ch == 0x2E || ch == 0x7E
            ) {
                sb.append(ch.toChar())
            } else {
                sb.append('%')
                sb.append(ch.toString(16).padStart(2, '0').uppercase())
            }
        }
        return sb.toString()
    }
}
