package dev.andyromero.data.local.hostcache.model

import kotlinx.serialization.Serializable

@Serializable
internal data class PropertyCache(
    val id: String,
    val hostId: String,
    val title: String,
    val description: String,
    val address: String,
    val pricePerNight: Double,
    val maxGuests: Int,
    val bedrooms: Int,
    val bathrooms: Int,
    val amenities: List<String>,
    val images: List<PropertyImageCache>,
    val isActive: Boolean,
    val createdAt: String,
)
