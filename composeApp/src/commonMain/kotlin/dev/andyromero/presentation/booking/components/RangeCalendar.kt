package dev.andyromero.presentation.booking.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber

@Composable
internal fun RangeCalendar(
    currentMonth: SimpleMonth,
    minMonth: SimpleMonth,
    maxMonth: SimpleMonth,
    availableDates: Set<SimpleDate>,
    unavailableDates: Set<SimpleDate>,
    selectedStart: SimpleDate?,
    selectedEnd: SimpleDate?,
    onMonthChange: (SimpleMonth) -> Unit,
    onDaySelected: (SimpleDate) -> Unit,
) {
    val monthLabel = remember(currentMonth) {
        "${monthDisplayName(currentMonth.month)} ${currentMonth.year}"
    }
    val today = remember { todaySimpleDateUtc() }
    val days = remember(currentMonth) { buildMonthCells(currentMonth) }
    val prevEnabled = currentMonth > minMonth
    val nextEnabled = currentMonth < maxMonth

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = { if (prevEnabled) onMonthChange(currentMonth.plusMonths(-1)) },
            enabled = prevEnabled,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Mes anterior",
            )
        }

        Text(
            text = monthLabel,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )

        IconButton(
            onClick = { if (nextEnabled) onMonthChange(currentMonth.plusMonths(1)) },
            enabled = nextEnabled,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Mes siguiente",
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        WEEKDAY_LABELS.forEach { label ->
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
            )
        }
    }

    Spacer(modifier = Modifier.height(4.dp))

    days.chunked(7).forEach { week ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            week.forEach { date ->
                val hasAvailabilityRules = availableDates.isNotEmpty()
                val isSelectable = date != null && (!hasAvailabilityRules || availableDates.contains(date))
                val isUnavailable = date != null && hasAvailabilityRules &&
                    (unavailableDates.contains(date) || !availableDates.contains(date))
                RangeDayCell(
                    date = date,
                    isSelectable = isSelectable,
                    isUnavailable = isUnavailable,
                    isStart = date != null && date == selectedStart,
                    isEnd = date != null && date == selectedEnd,
                    isInRange = date != null &&
                        selectedStart != null &&
                        selectedEnd != null &&
                        date > selectedStart &&
                        date < selectedEnd,
                    isToday = date != null && date == today,
                    onDaySelected = onDaySelected,
                )
            }
        }
    }
}

@Composable
private fun RowScope.RangeDayCell(
    date: SimpleDate?,
    isSelectable: Boolean,
    isUnavailable: Boolean,
    isStart: Boolean,
    isEnd: Boolean,
    isInRange: Boolean,
    isToday: Boolean,
    onDaySelected: (SimpleDate) -> Unit,
) {
    if (date == null) {
        Box(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
                .padding(2.dp),
        )
        return
    }

    val rangeColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    val selectedColor = MaterialTheme.colorScheme.primary
    val availableColor = Color(0xFF2E7D32)
    val unavailableColor = Color(0xFFC62828)
    val availabilityBackground = when {
        isStart || isEnd -> Color.Transparent
        isSelectable -> availableColor.copy(alpha = 0.16f)
        isUnavailable -> unavailableColor.copy(alpha = 0.16f)
        else -> Color.Transparent
    }
    val textColor = when {
        isStart || isEnd -> MaterialTheme.colorScheme.onPrimary
        isSelectable -> availableColor
        isUnavailable -> unavailableColor
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .padding(2.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (isInRange && !isStart && !isEnd) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp, vertical = 4.dp)
                    .background(rangeColor, RoundedCornerShape(8.dp)),
            )
        }

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    color = if (isStart || isEnd) selectedColor else availabilityBackground,
                    shape = CircleShape,
                )
                .border(
                    width = if (isToday && !(isStart || isEnd)) 1.5.dp else 0.dp,
                    color = if (isToday && !(isStart || isEnd)) MaterialTheme.colorScheme.primary
                    else Color.Transparent,
                    shape = CircleShape,
                )
                .clickable(enabled = isSelectable) { onDaySelected(date) },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = date.day.toString(),
                color = textColor,
                fontWeight = if (isStart || isEnd) FontWeight.Bold else FontWeight.Normal,
            )
        }
    }
}

private fun buildMonthCells(month: SimpleMonth): List<SimpleDate?> {
    val firstDay = month.atDay(1)
    val leadingBlanks = dayOfWeekIndex(firstDay)
    val totalDays = month.lengthOfMonth()
    val cells = mutableListOf<SimpleDate?>()
    repeat(leadingBlanks) { cells.add(null) }
    for (day in 1..totalDays) cells.add(month.atDay(day))
    while (cells.size % 7 != 0) cells.add(null)
    return cells
}

private fun dayOfWeekIndex(date: SimpleDate): Int {
    val dow = LocalDate(date.year, date.month, date.day).dayOfWeek
    // ISO: Mon=1..Sun=7 → Sun=0, Mon=1..Sat=6
    return dow.isoDayNumber % 7
}

private val WEEKDAY_LABELS = listOf("D", "L", "M", "M", "J", "V", "S")
