package dev.andyromero.presentation.auth.login

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
internal fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToRegister: (String?) -> Unit,
    onNavigateToBooking: (String) -> Unit,
    onNavigateToPropertyList: () -> Unit,
    onNavigateToHostTabs: () -> Unit,
    onNavigateToAdminBookings: () -> Unit,
    onShowMessage: (String) -> Unit = {},
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel.effects) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is LoginEffect.ShowError -> onShowMessage(effect.message)
                is LoginEffect.NavigateToRegister -> onNavigateToRegister(effect.returnPropertyId)
                is LoginEffect.NavigateToBooking -> onNavigateToBooking(effect.propertyId)
                LoginEffect.NavigateToPropertyList -> onNavigateToPropertyList()
                LoginEffect.NavigateToHostTabs -> onNavigateToHostTabs()
                LoginEffect.NavigateToAdminBookings -> onNavigateToAdminBookings()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Iniciar sesión", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            value = state.email,
            onValueChange = { viewModel.sendIntent(LoginIntent.EmailChanged(it)) },
            label = { Text("Email") },
            isError = state.emailError != null,
            supportingText = { state.emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            value = state.password,
            onValueChange = { viewModel.sendIntent(LoginIntent.PasswordChanged(it)) },
            label = { Text("Contraseña") },
            isError = state.passwordError != null,
            supportingText = { state.passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
            visualTransformation = PasswordVisualTransformation(),
        )

        Button(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            onClick = { viewModel.sendIntent(LoginIntent.Submit) },
            enabled = state.canSubmit && !state.isLoading,
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.height(18.dp))
            } else {
                Text("Entrar")
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            onClick = { viewModel.sendIntent(LoginIntent.GoToRegister) },
            enabled = !state.isLoading,
        ) {
            Text("Crear cuenta")
        }

        Button(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            onClick = { viewModel.sendIntent(LoginIntent.ContinueAsGuest) },
            enabled = !state.isLoading,
        ) {
            Text("Continuar sin cuenta")
        }
    }
}
