package dev.andyromero.presentation.auth.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
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
    val focusManager = LocalFocusManager.current
    val passwordFocusRequester = remember { FocusRequester() }

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.statusBars)
            .imePadding(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
        ) {
            // ═══════════════════════════════════════════════════════════════
            // TOP SECTION - Branding
            // ═══════════════════════════════════════════════════════════════
            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = "Alkilo",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Encuentra tu próximo alojamiento",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            // ═══════════════════════════════════════════════════════════════
            // FORM SECTION
            // ═══════════════════════════════════════════════════════════════
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Iniciar sesión",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Email Field
            Column {
                Text(
                    text = "Correo electrónico",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp),
                )

                OutlinedTextField(
                    value = state.email,
                    onValueChange = { viewModel.sendIntent(LoginIntent.EmailChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "tu@email.com",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        )
                    },
                    singleLine = true,
                    isError = state.emailError != null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { passwordFocusRequester.requestFocus() },
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = alkiloTextFieldColors(),
                )

                // Error message
                AnimatedVisibility(
                    visible = state.emailError != null,
                    enter = slideInVertically { -it / 2 } + fadeIn(),
                    exit = slideOutVertically { -it / 2 } + fadeOut(),
                ) {
                    Text(
                        text = state.emailError.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 6.dp, start = 4.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Password Field
            Column {
                Text(
                    text = "Contraseña",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp),
                )

                OutlinedTextField(
                    value = state.password,
                    onValueChange = { viewModel.sendIntent(LoginIntent.PasswordChanged(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(passwordFocusRequester),
                    placeholder = {
                        Text(
                            text = "Tu contraseña",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        )
                    },
                    singleLine = true,
                    isError = state.passwordError != null,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            if (state.canSubmit && !state.isLoading) {
                                viewModel.sendIntent(LoginIntent.Submit)
                            }
                        },
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = alkiloTextFieldColors(),
                )

                // Error message
                AnimatedVisibility(
                    visible = state.passwordError != null,
                    enter = slideInVertically { -it / 2 } + fadeIn(),
                    exit = slideOutVertically { -it / 2 } + fadeOut(),
                ) {
                    Text(
                        text = state.passwordError.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 6.dp, start = 4.dp),
                    )
                }
            }

            // ═══════════════════════════════════════════════════════════════
            // PRIMARY ACTION
            // ═══════════════════════════════════════════════════════════════
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.sendIntent(LoginIntent.Submit)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = state.canSubmit && !state.isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                ),
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(
                        text = "Entrar",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            // ═══════════════════════════════════════════════════════════════
            // SECONDARY ACTIONS
            // ═══════════════════════════════════════════════════════════════
            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { viewModel.sendIntent(LoginIntent.GoToRegister) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
            ) {
                Text(
                    text = "¿No tienes cuenta? Regístrate",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            // ═══════════════════════════════════════════════════════════════
            // TERTIARY ACTION (guest)
            // ═══════════════════════════════════════════════════════════════
            Spacer(modifier = Modifier.weight(1f))

            TextButton(
                onClick = { viewModel.sendIntent(LoginIntent.ContinueAsGuest) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                enabled = !state.isLoading,
            ) {
                Text(
                    text = "Explorar sin cuenta",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun alkiloTextFieldColors() = OutlinedTextFieldDefaults.colors(
    // Unfocused
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),

    // Focused
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),

    // Error
    errorBorderColor = MaterialTheme.colorScheme.error,
    errorContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
    errorTextColor = MaterialTheme.colorScheme.onSurface,

    // Disabled
    disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),

    // Cursor
    cursorColor = MaterialTheme.colorScheme.primary,
)
