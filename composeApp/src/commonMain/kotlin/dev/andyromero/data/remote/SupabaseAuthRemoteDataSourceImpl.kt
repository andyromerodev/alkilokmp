package dev.andyromero.data.remote

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal class SupabaseAuthRemoteDataSourceImpl(
    private val auth: Auth,
    private val postgrest: Postgrest,
) : AuthRemoteDataSourceContract {
    override suspend fun login(email: String, password: String): AuthRemoteResult {
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

        return AuthRemoteResult(
            session = RemoteSession(
                accessToken = userSession.accessToken,
                refreshToken = userSession.refreshToken,
                expiresAtEpochSeconds = userSession.expiresAt?.epochSeconds ?: 0L,
                userId = userId,
                email = authEmail,
            ),
            profile = profile,
        )
    }

    override suspend fun register(email: String, password: String, fullName: String): AuthRemoteResult {
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

        return AuthRemoteResult(
            session = RemoteSession(
                accessToken = userSession.accessToken,
                refreshToken = userSession.refreshToken,
                expiresAtEpochSeconds = userSession.expiresAt?.epochSeconds ?: 0L,
                userId = userId,
                email = authEmail,
            ),
            profile = profile,
        )
    }

    override suspend fun getProfile(userId: String): ProfileDto {
        return postgrest.from("profiles")
            .select {
                filter {
                    eq("id", userId)
                }
            }
            .decodeSingle<ProfileDto>()
    }

    override suspend fun logout() {
        auth.signOut()
    }
}
