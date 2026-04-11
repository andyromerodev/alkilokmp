package dev.andyromero.domain.usecase.property

import dev.andyromero.domain.repository.PropertyRepositoryContract

class GetPropertyByIdUseCase(
    private val repository: PropertyRepositoryContract,
) {
    suspend operator fun invoke(propertyId: String) = repository.getPropertyById(propertyId)
}
