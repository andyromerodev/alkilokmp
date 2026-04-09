package dev.andyromero.data.remote.property

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns

internal class SupabasePropertyRemoteDataSourceImpl(
    private val postgrest: Postgrest,
) : PropertyRemoteDataSourceContract {

    override suspend fun getProperties(): List<PropertyDto> {
        return postgrest.from("properties")
            .select(
                Columns.raw(
                    """
                    *,
                    property_images (*)
                    """.trimIndent()
                )
            )
            .decodeList<PropertyDto>()
    }
}
