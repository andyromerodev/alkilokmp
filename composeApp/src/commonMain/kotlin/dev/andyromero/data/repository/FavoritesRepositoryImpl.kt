package dev.andyromero.data.repository

import dev.andyromero.core.result.Result
import dev.andyromero.data.local.favorites.FavoritesStoreContract
import dev.andyromero.domain.repository.FavoritesRepositoryContract
import kotlinx.coroutines.flow.Flow

internal class FavoritesRepositoryImpl(
    private val store: FavoritesStoreContract,
) : FavoritesRepositoryContract {
    override fun observeFavorites(): Flow<Set<String>> = store.favorites

    override suspend fun toggleFavorite(propertyId: String): Result<Unit> {
        return try {
            store.toggleFavorite(propertyId)
            Result.Success(Unit)
        } catch (e: Throwable) {
            Result.Error(dev.andyromero.core.error.ErrorMapper.mapException(e))
        }
    }
}
