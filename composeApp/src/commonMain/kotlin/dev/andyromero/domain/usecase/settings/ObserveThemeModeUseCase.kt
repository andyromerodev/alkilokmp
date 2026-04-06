package dev.andyromero.domain.usecase.settings

import dev.andyromero.domain.repository.ThemeRepositoryContract

class ObserveThemeModeUseCase(
    private val themeRepository: ThemeRepositoryContract,
) {
    operator fun invoke() = themeRepository.observeThemeMode()
}
