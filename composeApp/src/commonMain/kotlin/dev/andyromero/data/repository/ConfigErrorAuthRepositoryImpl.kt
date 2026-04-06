package dev.andyromero.data.repository

import dev.andyromero.core.error.AppError
import dev.andyromero.core.result.Result
import dev.andyromero.domain.model.Profile
import dev.andyromero.domain.repository.AuthRepositoryContract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class ConfigErrorAuthRepositoryImpl(
    private val message: String,
) : AuthRepositoryContract {
    override val isLoggedIn: Flow<Boolean> = flowOf(false)
    override val currentProfile: Flow<Profile?> = flowOf(null)

    override suspend fun login(email: String, password: String): Result<Profile> {
        return Result.Error(AppError.Data.ValidationError(message))
    }

    override suspend fun register(email: String, password: String, fullName: String): Result<Profile> {
        return Result.Error(AppError.Data.ValidationError(message))
    }

    override suspend fun logout(): Result<Unit> {
        return Result.Error(AppError.Data.ValidationError(message))
    }

    override suspend fun getCurrentUser(): Result<Profile?> {
        return Result.Error(AppError.Data.ValidationError(message))
    }

    override suspend fun restoreSession(): Result<Boolean> {
        return Result.Error(AppError.Data.ValidationError(message))
    }
}

