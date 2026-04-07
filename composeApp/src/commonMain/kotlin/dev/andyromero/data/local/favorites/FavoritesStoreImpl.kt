package dev.andyromero.data.local.favorites

import dev.andyromero.core.platform.storage.KeyValueStoreFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class FavoritesStoreImpl(
    keyValueStoreFactory: KeyValueStoreFactory,
) : FavoritesStoreContract {
    private val store = keyValueStoreFactory.create("favorites_store")

    private val _favorites = MutableStateFlow(store.getStringSet(KEY_FAVORITES))
    override val favorites: Flow<Set<String>> = _favorites.asStateFlow()

    override suspend fun toggleFavorite(propertyId: String) {
        val current = _favorites.value.toMutableSet()
        if (current.contains(propertyId)) {
            current.remove(propertyId)
        } else {
            current.add(propertyId)
        }
        store.putStringSet(KEY_FAVORITES, current)
        _favorites.value = current
    }

    override suspend fun clear() {
        store.remove(KEY_FAVORITES)
        _favorites.value = emptySet()
    }

    private companion object {
        const val KEY_FAVORITES = "favorite_ids"
    }
}
