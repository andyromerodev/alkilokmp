package dev.andyromero.core.platform

actual class PlatformLogger actual constructor() : dev.andyromero.core.logging.Logger {
    actual override fun d(tag: String, message: String) { println("D/$tag: $message") }
    actual override fun w(tag: String, message: String, throwable: Throwable?) { println("W/$tag: $message ${throwable?.message ?: ""}") }
    actual override fun e(tag: String, message: String, throwable: Throwable?) { println("E/$tag: $message ${throwable?.message ?: ""}") }
}
