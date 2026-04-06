package dev.andyromero.data.local

import dev.andyromero.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemoryThemeSettingsStoreImpl : ThemeSettingsStoreContract {
    private val holder = MutableStateFlow(ThemeMode.SYSTEM)

    override val themeMode: Flow<ThemeMode> = holder.asStateFlow()

    override suspend fun setThemeMode(mode: ThemeMode) {
        holder.value = mode
    }
}
