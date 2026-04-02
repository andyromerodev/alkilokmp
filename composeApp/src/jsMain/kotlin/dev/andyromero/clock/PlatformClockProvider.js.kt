package dev.andyromero.clock

import kotlin.js.Date

actual class PlatformClockProvider actual constructor() : ClockProvider {
    actual override fun nowEpochSeconds(): Long = (Date.now() / 1000.0).toLong()
}

