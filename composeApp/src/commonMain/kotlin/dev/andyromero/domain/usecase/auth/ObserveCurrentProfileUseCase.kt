package dev.andyromero.domain.usecase.auth

import dev.andyromero.domain.repository.AuthRepositoryContract

class ObserveCurrentProfileUseCase(
    private val authRepository: AuthRepositoryContract,
) {
    operator fun invoke() = authRepository.currentProfile
}
