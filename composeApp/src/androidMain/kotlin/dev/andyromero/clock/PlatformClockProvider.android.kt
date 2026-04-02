package dev.andyromero.clock

actual class PlatformClockProvider actual constructor() : ClockProvider {
    actual override fun nowEpochSeconds(): Long = System.currentTimeMillis() / 1000L
}

