package dev.andyromero.data.local.favorites

import kotlinx.coroutines.flow.Flow

internal interface FavoritesStoreContract {
    val favorites: Flow<Set<String>>

    suspend fun toggleFavorite(propertyId: String)
    suspend fun clear()
}
