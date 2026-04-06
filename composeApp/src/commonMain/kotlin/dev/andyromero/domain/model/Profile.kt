package dev.andyromero.domain.model

data class Profile(
    val id: String,
    val email: String,
    val fullName: String?,
    val phone: String?,
    val role: UserRole,
    val createdAt: String,
)
