package dev.andyromero.presentation.auth

import dev.andyromero.domain.model.Profile

internal enum class AuthMode {
    LOGIN,
    REGISTER,
}

internal data class AuthState(
    val mode: AuthMode = AuthMode.LOGIN,
    val email: String = "",
    val password: String = "",
    val fullName: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val user: Profile? = null,
)

internal sealed interface AuthIntent {
    data class UpdateEmail(val value: String) : AuthIntent
    data class UpdatePassword(val value: String) : AuthIntent
    data class UpdateFullName(val value: String) : AuthIntent
    data object ToggleMode : AuthIntent
    data object Submit : AuthIntent
    data object Logout : AuthIntent
    data object ConsumeError : AuthIntent
}

internal sealed interface AuthEffect {
    data class ShowMessage(val message: String) : AuthEffect
    data object NavigateToHome : AuthEffect
}
