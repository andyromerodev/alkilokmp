package dev.andyromero.data.local.settings

import dev.andyromero.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

internal interface ThemeSettingsStoreContract {
    val themeMode: Flow<ThemeMode>

    suspend fun setThemeMode(mode: ThemeMode)
}
