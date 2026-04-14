package dev.andyromero.domain.usecase.property

import dev.andyromero.domain.model.PropertyType
import dev.andyromero.domain.repository.PropertyRepositoryContract

class GetPropertiesUseCase(
    private val repository: PropertyRepositoryContract,
) {
    suspend operator fun invoke(
        page: Int = 0,
        pageSize: Int = 100,
        type: PropertyType? = null,
        query: String? = null,
    ) = repository.getProperties(
        page = page,
        pageSize = pageSize,
        type = type,
        query = query,
    )
}
