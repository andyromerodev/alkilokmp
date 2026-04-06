package dev.andyromero.data.local

import dev.andyromero.domain.model.UserRole

data class Session(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long,
    val userId: String,
    val email: String,
    val role: UserRole,
) {
    val isExpired: Boolean
        get() = false
}
