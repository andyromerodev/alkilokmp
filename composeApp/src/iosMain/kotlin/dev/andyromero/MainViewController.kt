package dev.andyromero

import androidx.compose.ui.window.ComposeUIViewController
import dev.andyromero.di.initKoinIfNeeded

fun MainViewController() = ComposeUIViewController {
    initKoinIfNeeded()
    App()
}
