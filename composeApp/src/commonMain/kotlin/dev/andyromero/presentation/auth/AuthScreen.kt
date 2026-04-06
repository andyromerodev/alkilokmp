package dev.andyromero.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.SharedFlow

@Composable
internal fun AuthScreen(
    state: AuthState,
    effects: SharedFlow<AuthEffect>,
    onIntent: (AuthIntent) -> Unit,
) {
    LaunchedEffect(effects) {
        effects.collect { effect ->
            when (effect) {
                AuthEffect.NavigateToHome -> Unit
                is AuthEffect.ShowMessage -> Unit
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = if (state.mode == AuthMode.LOGIN) "Login" else "Registro",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(Modifier.height(12.dp))

        if (state.mode == AuthMode.REGISTER) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.fullName,
                onValueChange = { onIntent(AuthIntent.UpdateFullName(it)) },
                label = { Text("Nombre") },
            )
            Spacer(Modifier.height(8.dp))
        }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.email,
            onValueChange = { onIntent(AuthIntent.UpdateEmail(it)) },
            label = { Text("Correo") },
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.password,
            onValueChange = { onIntent(AuthIntent.UpdatePassword(it)) },
            label = { Text("Contrasena") },
            visualTransformation = PasswordVisualTransformation(),
        )

        state.errorMessage?.let {
            Spacer(Modifier.height(8.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading,
            onClick = { onIntent(AuthIntent.Submit) },
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                Text(if (state.mode == AuthMode.LOGIN) "Entrar" else "Crear cuenta")
            }
        }

        Spacer(Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onIntent(AuthIntent.ToggleMode) },
        ) {
            Text(if (state.mode == AuthMode.LOGIN) "Ir a registro" else "Ir a login")
        }

        if (state.user != null) {
            Spacer(Modifier.height(12.dp))
            Text("Sesion activa: ${state.user.email}")
            Spacer(Modifier.height(8.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onIntent(AuthIntent.Logout) },
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}
