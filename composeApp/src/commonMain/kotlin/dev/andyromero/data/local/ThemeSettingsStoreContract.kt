package dev.andyromero.data.local

import dev.andyromero.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface ThemeSettingsStoreContract {
    val themeMode: Flow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode)
}
