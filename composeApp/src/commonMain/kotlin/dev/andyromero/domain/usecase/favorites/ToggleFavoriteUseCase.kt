package dev.andyromero.domain.usecase.favorites

import dev.andyromero.domain.repository.FavoritesRepositoryContract

class ToggleFavoriteUseCase(
    private val repository: FavoritesRepositoryContract,
) {
    suspend operator fun invoke(propertyId: String) = repository.toggleFavorite(propertyId)
}
