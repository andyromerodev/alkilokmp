package dev.andyromero.core.platform

import android.util.Log

actual class PlatformLogger actual constructor() : dev.andyromero.core.logging.Logger {
    actual override fun d(tag: String, message: String) {
        Log.d(tag, message)
    }

    actual override fun w(tag: String, message: String, throwable: Throwable?) {
        Log.w(tag, message, throwable)
    }

    actual override fun e(tag: String, message: String, throwable: Throwable?) {
        Log.e(tag, message, throwable)
    }
}
