package dev.andyromero.domain.usecase.favorites

import dev.andyromero.domain.repository.FavoritesRepositoryContract

class ObserveFavoritesUseCase(
    private val repository: FavoritesRepositoryContract,
) {
    operator fun invoke() = repository.observeFavorites()
}
