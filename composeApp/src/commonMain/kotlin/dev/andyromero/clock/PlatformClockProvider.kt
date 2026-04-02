package dev.andyromero.clock

interface ClockProvider {
    fun nowEpochSeconds(): Long
}

expect class PlatformClockProvider() : ClockProvider

