package dev.andyromero.domain.repository

import dev.andyromero.core.result.Result
import dev.andyromero.domain.model.Property
import kotlinx.coroutines.flow.Flow

interface PropertyRepositoryContract {
    suspend fun getProperties(): Result<List<Property>>
    fun observeProperties(): Flow<List<Property>>
}
