package dev.andyromero.domain.usecase.favorites

import dev.andyromero.core.result.Result
import dev.andyromero.domain.model.Property
import dev.andyromero.domain.repository.PropertyRepositoryContract
import kotlinx.coroutines.flow.first

class GetFavoritePropertiesUseCase(
    private val propertyRepository: PropertyRepositoryContract,
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
) {
    suspend operator fun invoke(): Result<List<Property>> {
        return when (val result = propertyRepository.getProperties(page = 0, pageSize = 500)) {
            is Result.Success -> {
                val favorites = observeFavoritesUseCase().first()
                Result.Success(result.data.filter { property -> favorites.contains(property.id) })
            }

            is Result.Error -> result
        }
    }
}
