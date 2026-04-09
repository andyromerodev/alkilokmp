package dev.andyromero.data.remote.property

import dev.andyromero.domain.model.Property
import dev.andyromero.domain.model.PropertyImage
import dev.andyromero.domain.model.PropertyType

internal fun PropertyDto.toDomain(): Property {
    return Property(
        id = id,
        hostId = hostId,
        title = title,
        description = description.orEmpty(),
        address = address ?: location.orEmpty(),
        pricePerNight = pricePerNight,
        maxGuests = maxGuests,
        bedrooms = bedrooms ?: 0,
        bathrooms = bathrooms ?: 0,
        amenities = amenities,
        images = resolveImages(),
        isActive = isActive,
        createdAt = createdAt,
        type = type.toPropertyType(),
    )
}

private fun PropertyDto.resolveImages(): List<PropertyImage> {
    val relationImages = images
        ?.sortedBy { it.displayOrder }
        ?.map { it.toDomain() }
        .orEmpty()

    if (relationImages.isNotEmpty()) return relationImages

    val directUrl = imageUrl?.trim().orEmpty()
    if (directUrl.isBlank()) return emptyList()

    return listOf(
        PropertyImage(
            id = "${id}_main",
            propertyId = id,
            url = directUrl,
            isMain = true,
            order = 0,
        )
    )
}

private fun PropertyImageDto.toDomain(): PropertyImage {
    return PropertyImage(
        id = id,
        propertyId = propertyId,
        url = imageUrl,
        isMain = displayOrder == 0,
        order = displayOrder,
    )
}

private fun String?.toPropertyType(): PropertyType {
    val normalized = this
        ?.trim()
        ?.replace('-', '_')
        ?.replace(' ', '_')
        ?.uppercase()
    return when (normalized) {
        "BEACH_HOUSE" -> PropertyType.BEACH_HOUSE
        "POOL" -> PropertyType.POOL
        "APARTMENT" -> PropertyType.APARTMENT
        "VILLA" -> PropertyType.VILLA
        "CABIN" -> PropertyType.CABIN
        else -> PropertyType.OTHER
    }
}
