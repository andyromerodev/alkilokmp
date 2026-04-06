package dev.andyromero.core.platform

import dev.andyromero.core.logging.Logger

expect class PlatformLogger() : Logger {
    override fun d(tag: String, message: String)
    override fun w(tag: String, message: String, throwable: Throwable?)
    override fun e(tag: String, message: String, throwable: Throwable?)
}
