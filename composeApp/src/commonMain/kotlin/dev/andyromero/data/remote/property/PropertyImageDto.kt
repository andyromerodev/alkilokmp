package dev.andyromero.data.remote.property

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PropertyImageDto(
    val id: String = "",
    @SerialName("property_id") val propertyId: String = "",
    @SerialName("image_url") val imageUrl: String = "",
    @SerialName("display_order") val displayOrder: Int = 0,
)
