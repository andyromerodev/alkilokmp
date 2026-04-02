package dev.andyromero.learning

import dev.andyromero.clock.ClockProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LearningStoreTest {
    @Test
    fun nextDay_cycles_after_day_seven() {
        val store = LearningStore(clockProvider = FakeClockProvider(100L))
        var state = store.initialState()

        repeat(6) {
            state = store.reduce(state, LearningIntent.NextDay).state
        }
        assertEquals(7, state.currentDay)

        state = store.reduce(state, LearningIntent.NextDay).state
        assertEquals(1, state.currentDay)
    }

    @Test
    fun refreshClock_uses_injected_clock() {
        val clock = FakeClockProvider(500L)
        val store = LearningStore(clockProvider = clock)
        val state = store.initialState()

        clock.now = 777L
        val update = store.reduce(state, LearningIntent.RefreshClock)

        assertEquals(777L, update.state.lastRefreshEpochSeconds)
        assertNotNull(update.effect)
    }

    @Test
    fun toggleConcepts_flips_visibility_flag() {
        val store = LearningStore(clockProvider = FakeClockProvider(100L))
        val state = store.initialState()
        assertFalse(state.showConceptHints)

        val firstToggle = store.reduce(state, LearningIntent.ToggleConcepts).state
        assertTrue(firstToggle.showConceptHints)

        val secondToggle = store.reduce(firstToggle, LearningIntent.ToggleConcepts).state
        assertFalse(secondToggle.showConceptHints)
    }
}

private class FakeClockProvider(initialNow: Long) : ClockProvider {
    var now: Long = initialNow

    override fun nowEpochSeconds(): Long = now
}

