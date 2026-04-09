package dev.andyromero.domain.usecase.property

import dev.andyromero.domain.repository.PropertyRepositoryContract

class GetPropertiesUseCase(
    private val repository: PropertyRepositoryContract,
) {
    suspend operator fun invoke() = repository.getProperties()
}
