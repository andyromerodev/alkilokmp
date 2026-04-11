package dev.andyromero.data.remote.property

import dev.andyromero.core.logging.Logger
import dev.andyromero.domain.model.PropertyType
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.json.Json

internal class SupabasePropertyRemoteDataSourceImpl(
    private val postgrest: Postgrest,
    private val logger: Logger,
) : PropertyRemoteDataSourceContract {
    private companion object {
        const val TAG = "SupabasePropertyRemoteDS"
    }

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getProperties(
        page: Int,
        pageSize: Int,
        type: PropertyType?,
    ): List<PropertyDto> {
        logger.d(
            TAG,
            "getProperties request page=$page pageSize=$pageSize type=${type?.name ?: "ALL"}",
        )
        val from = page * pageSize
        val to = from + pageSize - 1
        val response = postgrest.from("properties")
            .select(
                columns = Columns.raw(
                    """
                    *,
                    property_images (*)
                    """.trimIndent()
                )
            ) {
                filter {
                    eq("is_active", true)
                    type?.let { selectedType ->
                        eq("type", selectedType.name)
                    }
                }
                order("created_at", Order.DESCENDING)
                range(from.toLong(), to.toLong())
            }
        val result = json.decodeFromString<List<PropertyDto>>(response.data)
        logger.d(TAG, "getProperties success count=${result.size}")
        return result
    }

    override suspend fun getPropertyById(id: String): PropertyDto {
        logger.d(TAG, "getPropertyById request id=$id")
        val response = postgrest.from("properties")
            .select(
                columns = Columns.raw(
                    """
                    *,
                    property_images (*)
                    """.trimIndent()
                )
            ) {
                filter {
                    eq("id", id)
                    eq("is_active", true)
                }
                range(0, 0)
            }
        val result = json.decodeFromString<List<PropertyDto>>(response.data)
            .firstOrNull()
            ?: error("Property not found for id=$id")
        logger.d(TAG, "getPropertyById success id=$id")
        return result
    }
}
