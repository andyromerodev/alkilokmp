package dev.andyromero.data.repository

import dev.andyromero.data.local.settings.ThemeSettingsStoreContract
import dev.andyromero.domain.model.ThemeMode
import dev.andyromero.domain.repository.ThemeRepositoryContract
import kotlinx.coroutines.flow.Flow

internal class ThemeRepositoryImpl(
    private val store: ThemeSettingsStoreContract,
) : ThemeRepositoryContract {
    override fun observeThemeMode(): Flow<ThemeMode> = store.themeMode

    override suspend fun setThemeMode(mode: ThemeMode) {
        store.setThemeMode(mode)
    }
}
