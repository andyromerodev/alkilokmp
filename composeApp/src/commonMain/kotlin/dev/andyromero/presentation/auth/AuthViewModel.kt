package dev.andyromero.presentation.auth

import dev.andyromero.core.result.Result
import dev.andyromero.domain.usecase.auth.LoginUseCase
import dev.andyromero.domain.usecase.auth.LogoutUseCase
import dev.andyromero.domain.usecase.auth.RegisterUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<AuthEffect>(extraBufferCapacity = 8)
    val effects: SharedFlow<AuthEffect> = _effects.asSharedFlow()

    fun onIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.UpdateEmail -> _state.value = _state.value.copy(email = intent.value)
            is AuthIntent.UpdatePassword -> _state.value = _state.value.copy(password = intent.value)
            is AuthIntent.UpdateFullName -> _state.value = _state.value.copy(fullName = intent.value)
            AuthIntent.ToggleMode -> {
                val mode = if (_state.value.mode == AuthMode.LOGIN) AuthMode.REGISTER else AuthMode.LOGIN
                _state.value = _state.value.copy(mode = mode, errorMessage = null)
            }

            AuthIntent.Submit -> submit()
            AuthIntent.Logout -> logout()
            AuthIntent.ConsumeError -> _state.value = _state.value.copy(errorMessage = null)
        }
    }

    private fun submit() {
        val current = _state.value
        if (current.isLoading) return

        scope.launch {
            _state.value = current.copy(isLoading = true, errorMessage = null)
            val result = if (current.mode == AuthMode.LOGIN) {
                loginUseCase(current.email.trim(), current.password)
            } else {
                registerUseCase(current.email.trim(), current.password, current.fullName.trim())
            }

            when (result) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        user = result.data,
                        errorMessage = null,
                    )
                    _effects.tryEmit(AuthEffect.ShowMessage("Bienvenido ${result.data.email}"))
                    _effects.tryEmit(AuthEffect.NavigateToHome)
                }

                is Result.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = result.error.message,
                    )
                    _effects.tryEmit(AuthEffect.ShowMessage(result.error.message))
                }
            }
        }
    }

    private fun logout() {
        scope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = logoutUseCase()) {
                is Result.Success -> {
                    _state.value = AuthState(mode = _state.value.mode)
                    _effects.tryEmit(AuthEffect.ShowMessage("Sesion cerrada"))
                }

                is Result.Error -> {
                    _state.value = _state.value.copy(isLoading = false, errorMessage = result.error.message)
                }
            }
        }
    }

    fun clear() {
        scope.coroutineContext.cancelChildren()
    }
}
