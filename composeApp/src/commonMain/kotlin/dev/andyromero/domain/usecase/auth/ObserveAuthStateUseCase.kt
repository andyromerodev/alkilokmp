package dev.andyromero.domain.usecase.auth

import dev.andyromero.domain.repository.AuthRepositoryContract

class ObserveAuthStateUseCase(
    private val authRepository: AuthRepositoryContract,
) {
    operator fun invoke() = authRepository.isLoggedIn
}
