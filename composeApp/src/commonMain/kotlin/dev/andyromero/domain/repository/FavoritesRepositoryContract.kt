package dev.andyromero.domain.repository

import dev.andyromero.core.result.Result
import kotlinx.coroutines.flow.Flow

interface FavoritesRepositoryContract {
    fun observeFavorites(): Flow<Set<String>>
    suspend fun toggleFavorite(propertyId: String): Result<Unit>
}
