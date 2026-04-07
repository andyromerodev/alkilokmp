package dev.andyromero.data.local.session

import dev.andyromero.domain.model.UserRole
import kotlin.time.Clock

internal data class Session(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long,
    val userId: String,
    val email: String,
    val role: UserRole,
) {
    val isExpired: Boolean
        get() = Clock.System.now().epochSeconds > expiresAt
}
