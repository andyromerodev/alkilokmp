package dev.andyromero

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import dev.andyromero.di.initKoinIfNeeded
import dev.andyromero.presentation.auth.AuthScreen
import dev.andyromero.presentation.auth.AuthViewModel
import org.koin.mp.KoinPlatform


@Composable
@Preview
fun App() {
    val viewModel = remember {
        initKoinIfNeeded()
        KoinPlatform.getKoin().get<AuthViewModel>()
    }
    val state by viewModel.state.collectAsState()
    DisposableEffect(viewModel) {
        onDispose { viewModel.clear() }
    }

    MaterialTheme {
        AuthScreen(
            state = state,
            effects = viewModel.effects,
            onIntent = viewModel::onIntent,
        )
    }
}
