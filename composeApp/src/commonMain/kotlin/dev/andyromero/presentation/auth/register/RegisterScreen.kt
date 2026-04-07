package dev.andyromero.presentation.auth.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
internal fun RegisterScreen(
    viewModel: RegisterViewModel,
    onNavigateToLogin: (String?) -> Unit,
    onNavigateToBooking: (String) -> Unit,
    onNavigateToPropertyList: () -> Unit,
    onShowMessage: (String) -> Unit = {},
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel.effects) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is RegisterEffect.ShowError -> onShowMessage(effect.message)
                is RegisterEffect.ShowSuccess -> onShowMessage(effect.message)
                is RegisterEffect.NavigateToLogin -> onNavigateToLogin(effect.returnPropertyId)
                is RegisterEffect.NavigateToBooking -> onNavigateToBooking(effect.propertyId)
                RegisterEffect.NavigateToPropertyList -> onNavigateToPropertyList()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Crear cuenta", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            value = state.fullName,
            onValueChange = { viewModel.sendIntent(RegisterIntent.FullNameChanged(it)) },
            label = { Text("Nombre completo") },
            isError = state.fullNameError != null,
            supportingText = { state.fullNameError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            value = state.email,
            onValueChange = { viewModel.sendIntent(RegisterIntent.EmailChanged(it)) },
            label = { Text("Email") },
            isError = state.emailError != null,
            supportingText = { state.emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            value = state.password,
            onValueChange = { viewModel.sendIntent(RegisterIntent.PasswordChanged(it)) },
            label = { Text("Contraseña") },
            isError = state.passwordError != null,
            supportingText = { state.passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
            visualTransformation = PasswordVisualTransformation(),
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            value = state.confirmPassword,
            onValueChange = { viewModel.sendIntent(RegisterIntent.ConfirmPasswordChanged(it)) },
            label = { Text("Confirmar contraseña") },
            isError = state.confirmPasswordError != null,
            supportingText = {
                state.confirmPasswordError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            },
            visualTransformation = PasswordVisualTransformation(),
        )

        Button(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            onClick = { viewModel.sendIntent(RegisterIntent.Submit) },
            enabled = state.canSubmit && !state.isLoading,
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.height(18.dp))
            } else {
                Text("Crear cuenta")
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            onClick = { viewModel.sendIntent(RegisterIntent.GoToLogin) },
            enabled = !state.isLoading,
        ) {
            Text("Ya tengo cuenta")
        }
    }
}
