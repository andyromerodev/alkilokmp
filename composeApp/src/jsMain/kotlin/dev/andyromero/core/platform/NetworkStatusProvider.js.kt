package dev.andyromero.core.platform

import kotlinx.browser.window

actual class NetworkStatusProvider actual constructor() {
    actual fun isOnline(): Boolean = window.navigator.onLine
}
