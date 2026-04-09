package dev.andyromero.data.remote.property

import dev.andyromero.domain.model.PropertyType

internal interface PropertyRemoteDataSourceContract {
    suspend fun getProperties(
        page: Int,
        pageSize: Int,
        type: PropertyType?,
    ): List<PropertyDto>
}
