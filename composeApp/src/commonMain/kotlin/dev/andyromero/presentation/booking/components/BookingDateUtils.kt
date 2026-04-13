package dev.andyromero.presentation.booking.components

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

const val DAY_IN_MILLIS = 86_400_000L

data class SimpleDate(val year: Int, val month: Int, val day: Int) : Comparable<SimpleDate> {
    override fun compareTo(other: SimpleDate): Int {
        return when {
            year != other.year -> year - other.year
            month != other.month -> month - other.month
            else -> day - other.day
        }
    }

    fun toIsoString(): String =
        "${year.toString().padStart(4, '0')}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
}

fun SimpleDate.toMonth(): SimpleMonth = SimpleMonth(year, month)

internal fun SimpleDate.toLocalDate(): LocalDate = LocalDate(year, month, day)

data class SimpleMonth(val year: Int, val month: Int) : Comparable<SimpleMonth> {
    override fun compareTo(other: SimpleMonth): Int {
        return when {
            year != other.year -> year - other.year
            else -> month - other.month
        }
    }

    fun plusMonths(delta: Int): SimpleMonth {
        var y = year
        var m = month + delta
        while (m > 12) {
            m -= 12
            y += 1
        }
        while (m < 1) {
            m += 12
            y -= 1
        }
        return SimpleMonth(y, m)
    }

    fun atDay(day: Int): SimpleDate = SimpleDate(year, month, day)

    fun lengthOfMonth(): Int {
        val nextFirst = if (month == 12) LocalDate(year + 1, 1, 1)
        else LocalDate(year, month + 1, 1)
        return nextFirst.plus(-1, DateTimeUnit.DAY).dayOfMonth
    }

    fun atEndOfMonth(): SimpleDate = SimpleDate(year, month, lengthOfMonth())
}

private val SPANISH_MONTHS = listOf(
    "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
    "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre",
)

fun monthDisplayName(month: Int): String = SPANISH_MONTHS[month - 1]

fun todayUtcMillis(): Long {
    val today = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.UTC).date
    return today.toEpochMilliseconds()
}

fun todaySimpleDateUtc(): SimpleDate = utcMillisToSimpleDate(todayUtcMillis())

fun utcMillisToSimpleDate(millis: Long): SimpleDate {
    val d = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC).date
    return SimpleDate(d.year, d.monthNumber, d.dayOfMonth)
}

fun simpleDateToUtcMillis(date: SimpleDate): Long {
    return LocalDate(date.year, date.month, date.day).toEpochMilliseconds()
}

fun parseIsoToSimpleDate(date: String): SimpleDate? {
    val parts = date.split("-")
    if (parts.size != 3) return null
    val year = parts[0].toIntOrNull() ?: return null
    val month = parts[1].toIntOrNull() ?: return null
    val day = parts[2].toIntOrNull() ?: return null
    if (month !in 1..12 || day !in 1..31) return null
    return SimpleDate(year, month, day)
}

fun formatDisplayDate(utcMillis: Long): String {
    val d = Instant.fromEpochMilliseconds(utcMillis).toLocalDateTime(TimeZone.UTC).date
    return "${d.dayOfMonth.toString().padStart(2, '0')}/${d.monthNumber.toString().padStart(2, '0')}/${d.year}"
}

fun toIsoDate(utcMillis: Long): String {
    return Instant.fromEpochMilliseconds(utcMillis).toLocalDateTime(TimeZone.UTC).date.toString()
}

fun normalizeUtcDate(utcMillis: Long): Long {
    val d = Instant.fromEpochMilliseconds(utcMillis).toLocalDateTime(TimeZone.UTC).date
    return SimpleDate(d.year, d.monthNumber, d.dayOfMonth).toLocalDate().toEpochMilliseconds()
}

fun addDays(date: SimpleDate, days: Int): SimpleDate {
    val result = date.toLocalDate().plus(days, DateTimeUnit.DAY)
    return SimpleDate(result.year, result.monthNumber, result.dayOfMonth)
}

fun isRangeAvailable(
    checkInUtc: Long,
    checkOutUtc: Long,
    availabilityByDate: Map<String, Boolean>,
): Boolean {
    if (checkOutUtc <= checkInUtc) return false
    var current = checkInUtc
    while (current < checkOutUtc) {
        if (availabilityByDate[toIsoDate(current)] != true) return false
        current += DAY_IN_MILLIS
    }
    return true
}

fun nightsBetween(checkInUtc: Long?, checkOutUtc: Long?): Int {
    val checkIn = checkInUtc ?: return 0
    val checkOut = checkOutUtc ?: return 0
    if (checkOut <= checkIn) return 0
    return ((checkOut - checkIn) / DAY_IN_MILLIS).toInt()
}

// LocalDate → epoch millis (midnight UTC)
private fun LocalDate.toEpochMilliseconds(): Long {
    val instant = this.atStartOfDayIn(TimeZone.UTC)
    return instant.toEpochMilliseconds()
}
