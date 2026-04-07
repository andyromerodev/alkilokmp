package dev.andyromero.data.local.hostcache.model

import kotlinx.serialization.Serializable

@Serializable
internal data class BookingRequestCache(
    val id: String,
    val propertyId: String,
    val clientId: String?,
    val guestName: String?,
    val guestPhone: String?,
    val startDate: String,
    val endDate: String,
    val guests: Int,
    val totalPrice: Double,
    val status: String,
    val notes: String?,
    val adminNotes: String?,
    val createdAt: String,
    val updatedAt: String,
    val confirmedAt: String?,
    val confirmedBy: String?,
)
