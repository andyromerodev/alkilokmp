package dev.andyromero.presentation.auth.login

import dev.andyromero.data.repository.ConfigErrorAuthRepositoryImpl
import dev.andyromero.domain.usecase.auth.LoginUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LoginViewModelTest {
    @Test
    fun submit_with_invalid_email_sets_email_error() = runTest {
        val vm = LoginViewModel(
            loginUseCase = LoginUseCase(ConfigErrorAuthRepositoryImpl("Config error")),
        )

        vm.sendIntent(LoginIntent.EmailChanged("bad-email"))
        vm.sendIntent(LoginIntent.PasswordChanged("123456"))
        vm.sendIntent(LoginIntent.Submit)

        assertEquals("Email inválido", vm.state.value.emailError)
    }

    @Test
    fun submit_with_config_error_emits_loading_false() = runTest {
        val vm = LoginViewModel(
            loginUseCase = LoginUseCase(ConfigErrorAuthRepositoryImpl("Config error")),
        )

        vm.sendIntent(LoginIntent.EmailChanged("andy@alkilo.dev"))
        vm.sendIntent(LoginIntent.PasswordChanged("123456"))
        vm.sendIntent(LoginIntent.Submit)

        kotlinx.coroutines.delay(120)
        assertTrue(!vm.state.value.isLoading)
    }
}
