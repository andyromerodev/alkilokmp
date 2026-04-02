package dev.andyromero.clock

import platform.Foundation.NSDate

actual class PlatformClockProvider actual constructor() : ClockProvider {
    actual override fun nowEpochSeconds(): Long = NSDate().timeIntervalSince1970.toLong()
}

