package dev.andyromero.domain.usecase.property

import dev.andyromero.core.result.Result
import dev.andyromero.domain.model.AvailabilityDay
import dev.andyromero.domain.repository.PropertyRepositoryContract

internal class GetPropertyAvailabilityUseCase(
    private val propertyRepository: PropertyRepositoryContract,
) {
    suspend operator fun invoke(
        propertyId: String,
        startDate: String,
        endDate: String,
    ): Result<List<AvailabilityDay>> {
        return propertyRepository.getPropertyAvailability(propertyId, startDate, endDate)
    }
}
