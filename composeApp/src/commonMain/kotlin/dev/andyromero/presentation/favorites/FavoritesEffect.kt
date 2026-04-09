package dev.andyromero.presentation.favorites

sealed interface FavoritesEffect {
    data class ShowError(val message: String) : FavoritesEffect
    data class NavigateToPropertyDetail(val propertyId: String) : FavoritesEffect
}
