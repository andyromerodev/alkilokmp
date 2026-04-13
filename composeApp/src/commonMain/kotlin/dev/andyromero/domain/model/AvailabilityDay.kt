package dev.andyromero.domain.model

data class AvailabilityDay(
    val date: String,
    val isAvailable: Boolean,
    val price: Double? = null,
)
