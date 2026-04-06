package dev.andyromero.presentation.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.mp.KoinPlatform

internal class AndroidAuthViewModel : ViewModel() {
    private val shared = KoinPlatform.getKoin().get<AuthViewModel>()

    val state: StateFlow<AuthState> = shared.state
    val effects: SharedFlow<AuthEffect> = shared.effects

    fun onIntent(intent: AuthIntent) {
        shared.onIntent(intent)
    }

    override fun onCleared() {
        shared.clear()
        super.onCleared()
    }
}
