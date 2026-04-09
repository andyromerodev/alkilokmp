package dev.andyromero.core.platform

import platform.Foundation.NSLog

actual class PlatformLogger actual constructor() : dev.andyromero.core.logging.Logger {
    actual override fun d(tag: String, message: String) {
        NSLog("D/$tag: $message")
    }

    actual override fun w(tag: String, message: String, throwable: Throwable?) {
        NSLog("W/$tag: $message ${throwable?.message ?: ""}")
    }

    actual override fun e(tag: String, message: String, throwable: Throwable?) {
        NSLog("E/$tag: $message ${throwable?.message ?: ""}")
    }
}
