package dev.andyromero.presentation.favorites

sealed interface FavoritesIntent {
    data object Load : FavoritesIntent
    data class ToggleFavorite(val propertyId: String) : FavoritesIntent
    data class OpenProperty(val propertyId: String) : FavoritesIntent
}
