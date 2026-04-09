package dev.andyromero.domain.model

data class PropertyImage(
    val id: String,
    val propertyId: String,
    val url: String,
    val isMain: Boolean,
    val order: Int,
)
