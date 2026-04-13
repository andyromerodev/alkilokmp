package dev.andyromero.domain.repository

import dev.andyromero.core.result.Result
import dev.andyromero.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface AuthRepositoryContract {
    val isLoggedIn: Flow<Boolean>
    val currentProfile: Flow<Profile?>

    suspend fun login(email: String, password: String): Result<Profile>
    suspend fun register(email: String, password: String, fullName: String): Result<Profile>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): Result<Profile?>
    suspend fun getProfile(userId: String): Result<Profile>
    suspend fun restoreSession(): Result<Boolean>
}
