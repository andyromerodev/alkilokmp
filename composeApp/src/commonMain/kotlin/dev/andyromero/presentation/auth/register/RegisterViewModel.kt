package dev.andyromero.presentation.auth.register

import dev.andyromero.core.error.AppError
import dev.andyromero.core.result.Result
import dev.andyromero.domain.usecase.auth.RegisterUseCase
import dev.andyromero.presentation.auth.shared.AuthValidators
import dev.andyromero.presentation.base.BaseViewModel

internal class RegisterViewModel(
    private val registerUseCase: RegisterUseCase,
    private val returnPropertyId: String? = null,
) : BaseViewModel<RegisterEffect, RegisterIntent, RegisterState>(RegisterState()) {
    override suspend fun handleIntent(intent: RegisterIntent) {
        when (intent) {
            is RegisterIntent.FullNameChanged -> updateFullName(intent.value)
            is RegisterIntent.EmailChanged -> updateEmail(intent.value)
            is RegisterIntent.PasswordChanged -> updatePassword(intent.value)
            is RegisterIntent.ConfirmPasswordChanged -> updateConfirmPassword(intent.value)
            RegisterIntent.Submit -> submit()
            RegisterIntent.GoToLogin -> emitEffect(RegisterEffect.NavigateToLogin(returnPropertyId))
        }
    }

    private fun updateFullName(value: String) {
        val error = if (value.isBlank()) "Nombre requerido" else null
        setState { copy(fullName = value, fullNameError = error) }
    }

    private fun updateEmail(value: String) {
        setState {
            copy(
                email = value,
                emailError = AuthValidators.emailError(value),
            )
        }
    }

    private fun updatePassword(value: String) {
        val error = AuthValidators.passwordError(value)
        setState { copy(password = value, passwordError = error) }
        updateConfirmPassword(currentState.confirmPassword)
    }

    private fun updateConfirmPassword(value: String) {
        val error = if (value.isBlank() || value == currentState.password) {
            null
        } else {
            "Las contraseñas no coinciden"
        }
        setState { copy(confirmPassword = value, confirmPasswordError = error) }
    }

    private fun submit() {
        val current = currentState
        if (current.isLoading) return

        val fullName = current.fullName.trim()
        val email = current.email.trim()
        val password = current.password
        val confirmPassword = current.confirmPassword

        updateFullName(fullName)
        updateEmail(email)
        updatePassword(password)
        updateConfirmPassword(confirmPassword)

        if (!currentState.canSubmit) return

        launch {
            setState { copy(isLoading = true) }
            when (val result = registerUseCase(email, password, fullName)) {
                is Result.Success -> {
                    setState { copy(isLoading = false) }
                    emitEffect(RegisterEffect.ShowSuccess("Cuenta creada exitosamente. Inicia sesión."))
                    emitEffect(RegisterEffect.NavigateToLogin(returnPropertyId))
                }

                is Result.Error -> {
                    setState { copy(isLoading = false) }
                    when (result.error) {
                        is AppError.Auth.EmailNotConfirmed -> {
                            emitEffect(
                                RegisterEffect.ShowSuccess("Revisa tu correo para confirmar la cuenta e inicia sesión."),
                            )
                            emitEffect(RegisterEffect.NavigateToLogin(returnPropertyId))
                        }

                        else -> emitEffect(RegisterEffect.ShowError(result.error.message))
                    }
                }
            }
        }
    }
}
