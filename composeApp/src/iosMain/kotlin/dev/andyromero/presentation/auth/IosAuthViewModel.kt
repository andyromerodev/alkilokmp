package dev.andyromero.presentation.auth

import dev.andyromero.di.initKoinIfNeeded
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.mp.KoinPlatform

internal class IosAuthViewModel {
    init {
        initKoinIfNeeded()
    }
    private val shared = KoinPlatform.getKoin().get<AuthViewModel>()

    val state: StateFlow<AuthState> = shared.state
    val effects: SharedFlow<AuthEffect> = shared.effects

    fun onIntent(intent: AuthIntent) {
        shared.onIntent(intent)
    }

    fun clear() {
        shared.clear()
    }
}
