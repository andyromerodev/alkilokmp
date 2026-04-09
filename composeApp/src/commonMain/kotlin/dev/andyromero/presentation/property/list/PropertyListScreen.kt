package dev.andyromero.presentation.property.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.andyromero.domain.model.Property
import dev.andyromero.domain.model.PropertyType
import dev.andyromero.presentation.property.list.components.PropertyCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PropertyListScreen(
    viewModel: PropertyListViewModel,
    onNavigateToDetail: (String) -> Unit,
    onShowMessage: (String) -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    var query by rememberSaveable { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<PropertyType?>(null) }

    LaunchedEffect(viewModel.effects) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is PropertyListEffect.ShowError -> onShowMessage(effect.message)
                is PropertyListEffect.NavigateToPropertyDetail -> onNavigateToDetail(effect.propertyId)
            }
        }
    }

    val filteredProperties = state.properties
        .filterByType(selectedType)
        .filterByQuery(query)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Explorar propiedades") },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Buscar por título o dirección") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true,
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    FilterChip(
                        selected = selectedType == null,
                        onClick = { selectedType = null },
                        label = { Text("Todos") },
                    )
                }
                items(PropertyType.entries) { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = if (selectedType == type) null else type },
                        label = { Text(type.toDisplayName()) },
                    )
                }
            }

            when {
                state.isLoading && state.properties.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                filteredProperties.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = state.errorMessage ?: "No hay propiedades disponibles con ese filtro",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        items(filteredProperties, key = { it.id }) { property ->
                            PropertyCard(
                                property = property,
                                isFavorite = state.favoriteIds.contains(property.id),
                                onToggleFavorite = {
                                    viewModel.sendIntent(PropertyListIntent.ToggleFavorite(property.id))
                                },
                                onOpenDetail = {
                                    viewModel.sendIntent(PropertyListIntent.OpenProperty(property.id))
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun List<Property>.filterByType(selectedType: PropertyType?): List<Property> {
    return if (selectedType == null) this else filter { it.type == selectedType }
}

private fun List<Property>.filterByQuery(query: String): List<Property> {
    if (query.isBlank()) return this
    val normalized = query.trim()
    return filter { property ->
        property.title.contains(normalized, ignoreCase = true) ||
            property.address.contains(normalized, ignoreCase = true)
    }
}

private fun PropertyType.toDisplayName(): String {
    return when (this) {
        PropertyType.BEACH_HOUSE -> "Playa"
        PropertyType.POOL -> "Piscina"
        PropertyType.APARTMENT -> "Apartamento"
        PropertyType.VILLA -> "Villa"
        PropertyType.CABIN -> "Cabaña"
        PropertyType.OTHER -> "Otros"
    }
}
