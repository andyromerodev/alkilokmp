package dev.andyromero.presentation.auth.login

internal sealed interface LoginIntent {
    data class EmailChanged(val value: String) : LoginIntent
    data class PasswordChanged(val value: String) : LoginIntent
    data object Submit : LoginIntent
    data object GoToRegister : LoginIntent
    data object ContinueAsGuest : LoginIntent
}
