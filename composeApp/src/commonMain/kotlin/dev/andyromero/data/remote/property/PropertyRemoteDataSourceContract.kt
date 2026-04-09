package dev.andyromero.data.remote.property

internal interface PropertyRemoteDataSourceContract {
    suspend fun getProperties(): List<PropertyDto>
}
