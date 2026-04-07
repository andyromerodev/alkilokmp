package dev.andyromero.data.local.settings

import dev.andyromero.core.platform.storage.KeyValueStoreFactory
import dev.andyromero.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class ThemeSettingsStoreImpl(
    keyValueStoreFactory: KeyValueStoreFactory,
) : ThemeSettingsStoreContract {
    private val store = keyValueStoreFactory.create("theme_settings_store")

    private val _themeMode = MutableStateFlow(readThemeMode())
    override val themeMode: Flow<ThemeMode> = _themeMode.asStateFlow()

    override suspend fun setThemeMode(mode: ThemeMode) {
        store.putString(KEY_THEME_MODE, mode.name)
        _themeMode.value = mode
    }

    private fun readThemeMode(): ThemeMode {
        val value = store.getString(KEY_THEME_MODE) ?: ThemeMode.SYSTEM.name
        return runCatching { ThemeMode.valueOf(value) }.getOrDefault(ThemeMode.SYSTEM)
    }

    private companion object {
        const val KEY_THEME_MODE = "theme_mode"
    }
}
