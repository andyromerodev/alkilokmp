package dev.andyromero.data.repository

import dev.andyromero.core.dispatcher.DispatcherProvider
import dev.andyromero.core.error.ErrorMapper
import dev.andyromero.core.logging.Logger
import dev.andyromero.core.result.Result
import dev.andyromero.data.remote.property.PropertyRemoteDataSourceContract
import dev.andyromero.data.remote.property.toDomain
import dev.andyromero.domain.model.Property
import dev.andyromero.domain.model.PropertyType
import dev.andyromero.domain.repository.PropertyRepositoryContract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

internal class SupabasePropertyRepositoryImpl(
    private val remoteDataSource: PropertyRemoteDataSourceContract,
    private val logger: Logger,
    private val dispatcherProvider: DispatcherProvider,
) : PropertyRepositoryContract {
    private val propertiesState = MutableStateFlow<List<Property>>(emptyList())

    override suspend fun getProperties(
        page: Int,
        pageSize: Int,
        type: PropertyType?,
    ): Result<List<Property>> {
        return withContext(dispatcherProvider.io) {
            try {
                val data = remoteDataSource.getProperties(
                    page = page,
                    pageSize = pageSize,
                    type = type,
                )
                    .map { it.toDomain() }
                propertiesState.value = if (page == 0) {
                    data
                } else {
                    (propertiesState.value + data).distinctBy { it.id }
                }
                Result.Success(data)
            } catch (e: Throwable) {
                logger.e("SupabasePropertyRepositoryImpl", "getProperties failed", e)
                Result.Error(ErrorMapper.mapException(e))
            }
        }
    }

    override suspend fun getPropertyById(id: String): Result<Property> {
        return withContext(dispatcherProvider.io) {
            try {
                val property = remoteDataSource.getPropertyById(id).toDomain()
                Result.Success(property)
            } catch (e: Throwable) {
                logger.e("SupabasePropertyRepositoryImpl", "getPropertyById failed for id=$id", e)
                Result.Error(ErrorMapper.mapException(e))
            }
        }
    }

    override fun observeProperties(): Flow<List<Property>> = propertiesState.asStateFlow()
}
