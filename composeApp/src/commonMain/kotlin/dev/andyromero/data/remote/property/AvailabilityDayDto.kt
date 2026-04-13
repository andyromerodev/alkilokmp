package dev.andyromero.data.remote.property

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AvailabilityDayDto(
    val date: String,
    @SerialName("is_available")
    val isAvailable: Boolean,
    val price: Double? = null,
)
