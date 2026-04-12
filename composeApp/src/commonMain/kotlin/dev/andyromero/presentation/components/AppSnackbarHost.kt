package dev.andyromero.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED

data class AppSnackbarMessage(
    val text: String,
    val actionLabel: String? = null,
    val withDismissAction: Boolean = false,
    val duration: SnackbarDuration = SnackbarDuration.Short,
)

class AppSnackbarManager {
    private val _channel = Channel<AppSnackbarMessage>(capacity = BUFFERED)

    suspend fun show(
        text: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
        duration: SnackbarDuration = SnackbarDuration.Short,
    ) {
        _channel.send(
            AppSnackbarMessage(
                text = text,
                actionLabel = actionLabel,
                withDismissAction = withDismissAction,
                duration = duration,
            )
        )
    }

    suspend fun showWithAction(
        text: String,
        actionLabel: String,
        withDismissAction: Boolean = false,
        duration: SnackbarDuration = SnackbarDuration.Short,
    ): SnackbarResult {
        val message = AppSnackbarMessage(
            text = text,
            actionLabel = actionLabel,
            withDismissAction = withDismissAction,
            duration = duration,
        )
        return try {
            _channel.send(message)
            SnackbarResult.ActionPerformed
        } catch (e: Exception) {
            SnackbarResult.Dismissed
        }
    }

    internal suspend fun receive(): AppSnackbarMessage = _channel.receive()
}

@Composable
fun rememberAppSnackbarManager(): AppSnackbarManager {
    return remember { AppSnackbarManager() }
}

@Composable
fun AppSnackbarHost(
    snackbarManager: AppSnackbarManager,
    modifier: Modifier = Modifier,
    hostState: SnackbarHostState = remember { SnackbarHostState() },
    onAction: ((String) -> Unit)? = null,
) {
    Box(modifier = modifier.fillMaxSize()) {
        SnackbarHost(
            hostState = hostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(8.dp),
        )

        LaunchedEffect(Unit) {
            while (true) {
                val message = snackbarManager.receive()
                val result = hostState.showSnackbar(
                    message = message.text,
                    actionLabel = message.actionLabel,
                    withDismissAction = message.withDismissAction,
                    duration = message.duration,
                )
                if (result == SnackbarResult.ActionPerformed && message.actionLabel != null) {
                    onAction?.invoke(message.text)
                }
            }
        }
    }
}

@Composable
fun ActionableSnackbar(
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    onDismiss: () -> Unit = {},
) {
    SnackbarHost(
        hostState = remember { SnackbarHostState() },
    ) {
        androidx.compose.material3.Snackbar(
            action = {
                Button(onClick = { onAction(); onDismiss() }) {
                    Text(actionLabel)
                }
            },
        ) {
            Text(message, color = MaterialTheme.colorScheme.inverseOnSurface)
        }
    }
}
