package dev.andyromero.data.local.hostcache.model

import kotlinx.serialization.Serializable

@Serializable
internal data class PropertyImageCache(
    val id: String,
    val propertyId: String,
    val url: String,
    val isMain: Boolean,
    val order: Int,
)
