package dev.andyromero.domain.usecase.settings

import dev.andyromero.domain.model.ThemeMode
import dev.andyromero.domain.repository.ThemeRepositoryContract

class UpdateThemeModeUseCase(
    private val themeRepository: ThemeRepositoryContract,
) {
    suspend operator fun invoke(mode: ThemeMode) = themeRepository.setThemeMode(mode)
}
