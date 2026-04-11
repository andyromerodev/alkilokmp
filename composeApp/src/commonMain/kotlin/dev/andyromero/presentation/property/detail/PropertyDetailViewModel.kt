package dev.andyromero.presentation.property.detail

import dev.andyromero.core.result.Result
import dev.andyromero.domain.usecase.property.GetPropertyByIdUseCase
import dev.andyromero.presentation.base.BaseViewModel

internal class PropertyDetailViewModel(
    private val propertyId: String,
    private val getPropertyByIdUseCase: GetPropertyByIdUseCase,
) : BaseViewModel<PropertyDetailEffect, PropertyDetailIntent, PropertyDetailState>(
    PropertyDetailState(),
) {

    init {
        sendIntent(PropertyDetailIntent.Load)
    }

    override suspend fun handleIntent(intent: PropertyDetailIntent) {
        when (intent) {
            PropertyDetailIntent.Load -> load()
            PropertyDetailIntent.BookNow -> onBookNow()
            PropertyDetailIntent.Back -> emitEffect(PropertyDetailEffect.NavigateBack)
        }
    }

    private fun load() {
        launch {
            setState { copy(isLoading = true, errorMessage = null) }
            when (val result = getPropertyByIdUseCase(propertyId)) {
                is Result.Success -> {
                    setState {
                        copy(
                            isLoading = false,
                            property = result.data,
                            errorMessage = null,
                        )
                    }
                }

                is Result.Error -> {
                    setState {
                        copy(
                            isLoading = false,
                            errorMessage = result.error.message,
                        )
                    }
                    emitEffect(PropertyDetailEffect.ShowError(result.error.message))
                }
            }
        }
    }

    private fun onBookNow() {
        val resolvedPropertyId = currentState.property?.id ?: propertyId
        emitEffect(PropertyDetailEffect.NavigateToBooking(resolvedPropertyId))
    }
}
