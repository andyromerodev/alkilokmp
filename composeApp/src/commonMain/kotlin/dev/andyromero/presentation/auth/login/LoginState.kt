package dev.andyromero.presentation.auth.login

internal data class LoginState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
) {
    val canSubmit: Boolean
        get() = email.isNotBlank() && password.length >= 6 && emailError == null && passwordError == null
}
