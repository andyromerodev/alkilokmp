package dev.andyromero.data.repository

import dev.andyromero.core.dispatcher.DispatcherProvider
import dev.andyromero.core.error.AppError
import dev.andyromero.core.error.ErrorMapper
import dev.andyromero.core.logging.Logger
import dev.andyromero.core.platform.NetworkStatusProvider
import dev.andyromero.core.result.Result
import dev.andyromero.data.local.session.AuthLocalDataSourceContract
import dev.andyromero.data.local.session.Session
import dev.andyromero.data.remote.AuthRemoteDataSourceContract
import dev.andyromero.data.remote.toDomain
import dev.andyromero.domain.model.Profile
import dev.andyromero.domain.repository.AuthRepositoryContract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class SupabaseAuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSourceContract,
    private val localDataSource: AuthLocalDataSourceContract,
    private val networkStatusProvider: NetworkStatusProvider,
    private val logger: Logger,
    private val dispatcherProvider: DispatcherProvider,
) : AuthRepositoryContract {

    override val isLoggedIn: Flow<Boolean> = localDataSource.session.map { it != null }

    override val currentProfile: Flow<Profile?> = localDataSource.session.map { session ->
        session?.let {
            Profile(
                id = it.userId,
                email = it.email,
                fullName = null,
                phone = null,
                role = it.role,
                createdAt = "",
            )
        }
    }

    override suspend fun login(email: String, password: String): Result<Profile> {
        return withContext(dispatcherProvider.io) {
            if (!networkStatusProvider.isOnline()) {
                return@withContext Result.Error(AppError.Network.NoConnection())
            }
            try {
                val authResult = remoteDataSource.login(email = email, password = password)
                val profile = authResult.profile.toDomain(fallbackEmail = authResult.session.email)
                localDataSource.saveSession(
                    Session(
                        accessToken = authResult.session.accessToken,
                        refreshToken = authResult.session.refreshToken,
                        expiresAt = authResult.session.expiresAtEpochSeconds,
                        userId = authResult.session.userId,
                        email = profile.email,
                        role = profile.role,
                    )
                )

                logger.d("SupabaseAuthRepositoryImpl", "Login ok for ${profile.email}")
                Result.Success(profile)
            } catch (e: Throwable) {
                logger.e("SupabaseAuthRepositoryImpl", "Login failed", e)
                Result.Error(ErrorMapper.mapException(e))
            }
        }
    }

    override suspend fun register(email: String, password: String, fullName: String): Result<Profile> {
        return withContext(dispatcherProvider.io) {
            if (!networkStatusProvider.isOnline()) {
                return@withContext Result.Error(AppError.Network.NoConnection())
            }
            try {
                val authResult = remoteDataSource.register(email = email, password = password, fullName = fullName)
                val profile = authResult.profile.toDomain(fallbackEmail = authResult.session.email)
                localDataSource.saveSession(
                    Session(
                        accessToken = authResult.session.accessToken,
                        refreshToken = authResult.session.refreshToken,
                        expiresAt = authResult.session.expiresAtEpochSeconds,
                        userId = authResult.session.userId,
                        email = profile.email,
                        role = profile.role,
                    )
                )

                Result.Success(profile)
            } catch (e: Throwable) {
                logger.e("SupabaseAuthRepositoryImpl", "Register failed", e)
                Result.Error(ErrorMapper.mapException(e))
            }
        }
    }

    override suspend fun logout(): Result<Unit> {
        return withContext(dispatcherProvider.io) {
            try {
                remoteDataSource.logout()
                localDataSource.clearSession()
                Result.Success(Unit)
            } catch (e: Throwable) {
                localDataSource.clearSession()
                Result.Error(ErrorMapper.mapException(e))
            }
        }
    }

    override suspend fun getCurrentUser(): Result<Profile?> {
        return withContext(dispatcherProvider.io) {
            if (!networkStatusProvider.isOnline()) {
                return@withContext Result.Error(AppError.Network.NoConnection())
            }
            try {
                val session = localDataSource.getSession() ?: return@withContext Result.Success(null)
                val profileDto = remoteDataSource.getProfile(session.userId)
                Result.Success(profileDto.toDomain(fallbackEmail = session.email))
            } catch (e: Throwable) {
                Result.Error(ErrorMapper.mapException(e))
            }
        }
    }

    override suspend fun restoreSession(): Result<Boolean> {
        return withContext(dispatcherProvider.io) {
            Result.Success(localDataSource.getSession() != null)
        }
    }
}
