package dev.andyromero.domain.repository

import dev.andyromero.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface ThemeRepositoryContract {
    fun observeThemeMode(): Flow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode)
}
