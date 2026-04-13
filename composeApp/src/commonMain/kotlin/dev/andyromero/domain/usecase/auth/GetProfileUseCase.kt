package dev.andyromero.domain.usecase.auth

import dev.andyromero.core.result.Result
import dev.andyromero.domain.model.Profile
import dev.andyromero.domain.repository.AuthRepositoryContract

internal class GetProfileUseCase(
    private val authRepository: AuthRepositoryContract,
) {
    suspend operator fun invoke(userId: String): Result<Profile> {
        return authRepository.getProfile(userId)
    }
}
