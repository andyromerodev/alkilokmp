package dev.andyromero.data.repository

import dev.andyromero.core.dispatcher.DispatcherProvider
import dev.andyromero.core.error.ErrorMapper
import dev.andyromero.core.logging.Logger
import dev.andyromero.core.result.Result
import dev.andyromero.data.remote.property.PropertyDto
import dev.andyromero.data.remote.property.toDomain
import dev.andyromero.domain.model.Property
import dev.andyromero.domain.repository.PropertyRepositoryContract
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

internal class SupabasePropertyRepositoryImpl(
    private val postgrest: Postgrest,
    private val logger: Logger,
    private val dispatcherProvider: DispatcherProvider,
) : PropertyRepositoryContract {
    private val propertiesState = MutableStateFlow<List<Property>>(emptyList())

    override suspend fun getProperties(): Result<List<Property>> {
        return withContext(dispatcherProvider.io) {
            try {
                val data = postgrest.from("properties")
                    .select(
                        Columns.raw(
                            """
                            *,
                            property_images (*)
                            """.trimIndent()
                        )
                    )
                    .decodeList<PropertyDto>()
                    .map { it.toDomain() }

                propertiesState.value = data
                Result.Success(data)
            } catch (e: Throwable) {
                logger.e("SupabasePropertyRepositoryImpl", "getProperties failed", e)
                Result.Error(ErrorMapper.mapException(e))
            }
        }
    }

    override fun observeProperties(): Flow<List<Property>> = propertiesState.asStateFlow()
}
