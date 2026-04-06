package dev.andyromero.domain.usecase.auth

import dev.andyromero.domain.repository.AuthRepositoryContract

class RestoreSessionUseCase(
    private val authRepository: AuthRepositoryContract,
) {
    suspend operator fun invoke() = authRepository.restoreSession()
}
