package dev.andyromero.domain.usecase.auth

import dev.andyromero.domain.repository.AuthRepositoryContract

class LoginUseCase(
    private val authRepository: AuthRepositoryContract,
) {
    suspend operator fun invoke(email: String, password: String) = authRepository.login(email, password)
}
