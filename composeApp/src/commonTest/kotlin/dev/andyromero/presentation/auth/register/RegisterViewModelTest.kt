package dev.andyromero.presentation.auth.register

import dev.andyromero.data.repository.ConfigErrorAuthRepositoryImpl
import dev.andyromero.domain.usecase.auth.RegisterUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RegisterViewModelTest {
    @Test
    fun submit_with_password_mismatch_sets_confirm_password_error() = runTest {
        val vm = RegisterViewModel(
            registerUseCase = RegisterUseCase(ConfigErrorAuthRepositoryImpl("Config error")),
        )

        vm.sendIntent(RegisterIntent.FullNameChanged("Andy"))
        vm.sendIntent(RegisterIntent.EmailChanged("andy@alkilo.dev"))
        vm.sendIntent(RegisterIntent.PasswordChanged("123456"))
        vm.sendIntent(RegisterIntent.ConfirmPasswordChanged("111111"))
        vm.sendIntent(RegisterIntent.Submit)

        assertEquals("Las contraseñas no coinciden", vm.state.value.confirmPasswordError)
    }

    @Test
    fun submit_with_config_error_finishes_loading() = runTest {
        val vm = RegisterViewModel(
            registerUseCase = RegisterUseCase(ConfigErrorAuthRepositoryImpl("Config error")),
        )

        vm.sendIntent(RegisterIntent.FullNameChanged("Andy"))
        vm.sendIntent(RegisterIntent.EmailChanged("andy@alkilo.dev"))
        vm.sendIntent(RegisterIntent.PasswordChanged("123456"))
        vm.sendIntent(RegisterIntent.ConfirmPasswordChanged("123456"))
        vm.sendIntent(RegisterIntent.Submit)

        kotlinx.coroutines.delay(120)
        assertTrue(!vm.state.value.isLoading)
    }
}
