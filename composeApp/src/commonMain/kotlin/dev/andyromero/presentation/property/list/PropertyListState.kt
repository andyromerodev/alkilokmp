package dev.andyromero.presentation.property.list

import dev.andyromero.domain.model.Property
import dev.andyromero.domain.model.PropertyType

data class PropertyListState(
    val isLoading: Boolean = false,
    val isPaging: Boolean = false,
    val canLoadMore: Boolean = true,
    val nextPage: Int = 0,
    val selectedType: PropertyType? = null,
    val searchQuery: String = "",
    val firstVisibleItemIndex: Int = 0,
    val firstVisibleItemScrollOffset: Int = 0,
    val properties: List<Property> = emptyList(),
    val favoriteIds: Set<String> = emptySet(),
    val errorMessage: String? = null,
)
