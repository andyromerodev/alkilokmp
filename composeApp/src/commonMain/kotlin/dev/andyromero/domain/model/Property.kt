package dev.andyromero.domain.model

data class Property(
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
    val images: List<PropertyImage>,
    val isActive: Boolean,
    val createdAt: String,
    val type: PropertyType = PropertyType.OTHER,
) {
    val mainImageUrl: String?
        get() = images.firstOrNull { it.isMain }?.url ?: images.firstOrNull()?.url
}
