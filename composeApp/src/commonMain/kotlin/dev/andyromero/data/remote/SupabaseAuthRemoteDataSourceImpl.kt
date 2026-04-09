package dev.andyromero.data.remote

import dev.andyromero.core.logging.Logger
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal class SupabaseAuthRemoteDataSourceImpl(
    private val auth: Auth,
    private val postgrest: Postgrest,
    private val logger: Logger,
) : AuthRemoteDataSourceContract {
    private companion object {
        const val TAG = "SupabaseAuthRemoteDS"
    }

    override suspend fun login(email: String, password: String): AuthRemoteResult {
        logger.d(TAG, "login request for email=$email")
        return runCatching {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            val userSession = auth.currentSessionOrNull()
                ?: error("Supabase login completed without session")

            val userId = userSession.user?.id
                ?: error("Supabase login returned session without user id")

            val authEmail = userSession.user?.email ?: email
            val profile = getProfile(userId)

            AuthRemoteResult(
                session = RemoteSession(
                    accessToken = userSession.accessToken,
                    refreshToken = userSession.refreshToken,
                    expiresAtEpochSeconds = userSession.expiresAt?.epochSeconds ?: 0L,
                    userId = userId,
                    email = authEmail,
                ),
                profile = profile,
            )
        }.onSuccess {
            logger.d(TAG, "login success userId=${it.session.userId}")
        }.onFailure { throwable ->
            logger.e(TAG, "login failed", throwable)
        }.getOrThrow()
    }

    override suspend fun register(email: String, password: String, fullName: String): AuthRemoteResult {
        logger.d(TAG, "register request for email=$email")
        return runCatching {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
                data = buildJsonObject {
                    put("full_name", fullName)
                }
            }

            val userSession = auth.currentSessionOrNull()
                ?: error("Supabase register completed without session")

            val userId = userSession.user?.id
                ?: error("Supabase register returned session without user id")

            val authEmail = userSession.user?.email ?: email
            val profile = getProfile(userId)

            AuthRemoteResult(
                session = RemoteSession(
                    accessToken = userSession.accessToken,
                    refreshToken = userSession.refreshToken,
                    expiresAtEpochSeconds = userSession.expiresAt?.epochSeconds ?: 0L,
                    userId = userId,
                    email = authEmail,
                ),
                profile = profile,
            )
        }.onSuccess {
            logger.d(TAG, "register success userId=${it.session.userId}")
        }.onFailure { throwable ->
            logger.e(TAG, "register failed", throwable)
        }.getOrThrow()
    }

    override suspend fun getProfile(userId: String): ProfileDto {
        logger.d(TAG, "getProfile request userId=$userId")
        val profile = postgrest.from("profiles")
            .select {
                filter {
                    eq("id", userId)
                }
            }
            .decodeSingle<ProfileDto>()
        logger.d(TAG, "getProfile success userId=$userId")
        return profile
    }

    override suspend fun logout() {
        logger.d(TAG, "logout request")
        auth.signOut()
        logger.d(TAG, "logout success")
    }
}
