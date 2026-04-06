package dev.andyromero.data.remote

import dev.andyromero.domain.model.Profile
import dev.andyromero.domain.model.UserRole

internal fun ProfileDto.toDomain(fallbackEmail: String): Profile {
    return Profile(
        id = id,
        email = email ?: fallbackEmail,
        fullName = fullName,
        phone = phone,
        role = runCatching { UserRole.valueOf(role.uppercase()) }.getOrDefault(UserRole.CLIENT),
        createdAt = createdAt,
    )
}
