package dev.andyromero.presentation.auth.register

internal sealed interface RegisterIntent {
    data class FullNameChanged(val value: String) : RegisterIntent
    data class EmailChanged(val value: String) : RegisterIntent
    data class PasswordChanged(val value: String) : RegisterIntent
    data class ConfirmPasswordChanged(val value: String) : RegisterIntent
    data object Submit : RegisterIntent
    data object GoToLogin : RegisterIntent
}
