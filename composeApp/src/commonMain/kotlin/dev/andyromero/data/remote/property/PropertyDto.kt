package dev.andyromero.data.remote.property

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PropertyDto(
    val id: String = "",
    @SerialName("host_id") val hostId: String = "",
    val title: String = "",
    val description: String? = null,
    val address: String? = null,
    @SerialName("location") val location: String? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("price_per_night") val pricePerNight: Double = 0.0,
    @SerialName("max_guests") val maxGuests: Int = 0,
    val bedrooms: Int? = null,
    val bathrooms: Int? = null,
    val amenities: List<String> = emptyList(),
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("property_images") val images: List<PropertyImageDto>? = null,
    @SerialName("image_url") val imageUrl: String? = null,
)
