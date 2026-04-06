package dev.andyromero.domain.usecase.auth

import dev.andyromero.domain.repository.AuthRepositoryContract

class RegisterUseCase(
    private val authRepository: AuthRepositoryContract,
) {
    suspend operator fun invoke(email: String, password: String, fullName: String) =
        authRepository.register(email, password, fullName)
}
