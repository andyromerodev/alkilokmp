package dev.andyromero.presentation.property.detail

sealed interface PropertyDetailIntent {
    data object Load : PropertyDetailIntent
    data object BookNow : PropertyDetailIntent
    data object Back : PropertyDetailIntent
}
