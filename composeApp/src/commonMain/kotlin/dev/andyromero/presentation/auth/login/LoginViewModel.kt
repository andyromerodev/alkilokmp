package dev.andyromero.presentation.auth.login

import dev.andyromero.core.error.AppError
import dev.andyromero.core.result.Result
import dev.andyromero.domain.model.UserRole
import dev.andyromero.domain.usecase.auth.LoginUseCase
import dev.andyromero.presentation.auth.shared.AuthValidators
import dev.andyromero.presentation.base.BaseViewModel

internal class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val returnPropertyId: String? = null,
) : BaseViewModel<LoginEffect, LoginIntent, LoginState>(LoginState()) {
    override suspend fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.EmailChanged -> updateEmail(intent.value)
            is LoginIntent.PasswordChanged -> updatePassword(intent.value)
            LoginIntent.Submit -> submit()
            LoginIntent.GoToRegister -> emitEffect(LoginEffect.NavigateToRegister(returnPropertyId))
            LoginIntent.ContinueAsGuest -> emitEffect(LoginEffect.NavigateToPropertyList)
        }
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
        setState {
            copy(
                password = value,
                passwordError = AuthValidators.passwordError(value),
            )
        }
    }

    private fun submit() {
        val current = currentState
        if (current.isLoading) return

        val email = current.email.trim()
        val password = current.password

        updateEmail(email)
        updatePassword(password)
        if (!currentState.canSubmit) return

        launch {
            setState { copy(isLoading = true) }
            when (val result = loginUseCase(email, password)) {
                is Result.Success -> {
                    setState { copy(isLoading = false) }
                    val propertyId = returnPropertyId
                    if (propertyId != null) {
                        when (result.data.role) {
                            UserRole.CLIENT -> emitEffect(LoginEffect.NavigateToBooking(propertyId))
                            UserRole.HOST -> emitEffect(LoginEffect.NavigateToHostTabs)
                            UserRole.ADMIN -> emitEffect(LoginEffect.NavigateToAdminBookings)
                        }
                    } else {
                        when (result.data.role) {
                            UserRole.HOST -> emitEffect(LoginEffect.NavigateToHostTabs)
                            UserRole.ADMIN -> emitEffect(LoginEffect.NavigateToAdminBookings)
                            UserRole.CLIENT -> emitEffect(LoginEffect.NavigateToPropertyList)
                        }
                    }
                }

                is Result.Error -> {
                    setState { copy(isLoading = false) }
                    val message = when (result.error) {
                        is AppError.Auth.EmailNotConfirmed -> "Revisa tu correo para confirmar la cuenta"
                        else -> result.error.message
                    }
                    emitEffect(LoginEffect.ShowError(message))
                }
            }
        }
    }
}
