package dev.andyromero.presentation.auth

import dev.andyromero.data.repository.ConfigErrorAuthRepositoryImpl
import dev.andyromero.domain.usecase.auth.LoginUseCase
import dev.andyromero.domain.usecase.auth.LogoutUseCase
import dev.andyromero.domain.usecase.auth.RegisterUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class AuthViewModelTest {
    @Test
    fun submit_login_shows_error_when_config_is_invalid() = runTest {
        val repository = ConfigErrorAuthRepositoryImpl("Config error")
        val viewModel = AuthViewModel(
            loginUseCase = LoginUseCase(repository),
            registerUseCase = RegisterUseCase(repository),
            logoutUseCase = LogoutUseCase(repository),
        )

        viewModel.onIntent(AuthIntent.UpdateEmail("andy@alkilo.dev"))
        viewModel.onIntent(AuthIntent.UpdatePassword("1234"))
        viewModel.onIntent(AuthIntent.Submit)

        kotlinx.coroutines.delay(100)
        assertTrue(viewModel.state.value.errorMessage != null)
    }
}
