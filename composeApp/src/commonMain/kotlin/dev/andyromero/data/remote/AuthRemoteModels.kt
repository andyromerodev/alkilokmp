package dev.andyromero.data.remote

internal data class RemoteSession(
    val accessToken: String,
    val refreshToken: String,
    val expiresAtEpochSeconds: Long,
    val userId: String,
    val email: String,
)

internal data class AuthRemoteResult(
    val session: RemoteSession,
    val profile: ProfileDto,
)

