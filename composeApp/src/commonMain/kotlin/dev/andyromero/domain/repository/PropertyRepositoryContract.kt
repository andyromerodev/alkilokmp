package dev.andyromero.domain.repository

import dev.andyromero.core.result.Result
import dev.andyromero.domain.model.Property
import dev.andyromero.domain.model.PropertyType
import kotlinx.coroutines.flow.Flow

interface PropertyRepositoryContract {
    suspend fun getProperties(
        page: Int = 0,
        pageSize: Int = 20,
        type: PropertyType? = null,
    ): Result<List<Property>>

    fun observeProperties(): Flow<List<Property>>
}
