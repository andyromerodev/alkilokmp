package dev.andyromero.data.remote

internal interface AuthRemoteDataSourceContract {
    suspend fun login(email: String, password: String): AuthRemoteResult
    suspend fun register(email: String, password: String, fullName: String): AuthRemoteResult
    suspend fun getProfile(userId: String): ProfileDto
    suspend fun logout()
}
