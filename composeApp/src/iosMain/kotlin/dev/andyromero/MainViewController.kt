package dev.andyromero

import androidx.compose.ui.window.ComposeUIViewController
import dev.andyromero.di.initKoinIfNeeded
import platform.Foundation.NSLog

fun MainViewController() = ComposeUIViewController {
    NSLog("🔵 [Boot] MainViewController — start")
    try {
        initKoinIfNeeded()
        NSLog("🔵 [Boot] Koin initialized OK")
    } catch (e: Throwable) {
        NSLog("🔴 [Boot] Koin FAILED: ${e.message}")
        throw e
    }
    
    App()
}
