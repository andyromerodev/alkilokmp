package dev.andyromero.presentation.property.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Search
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.andyromero.domain.model.Property
import dev.andyromero.domain.model.PropertyType
import dev.andyromero.presentation.property.list.components.PropertyCard

@Composable
internal fun PropertyListScreen(
    viewModel: PropertyListViewModel,
    onNavigateToDetail: (String) -> Unit,
    onShowMessage: (String) -> Unit = {},
    userName: String = "",
    contentPadding: PaddingValues = PaddingValues(),
) {
    val state by viewModel.state.collectAsState()
    var query by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()

    // UI-local: header visibility driven purely by scroll direction
    var isHeaderVisible by remember { mutableStateOf(true) }
    var prevFirstIndex by remember { mutableStateOf(0) }
    var prevScrollOffset by remember { mutableStateOf(0) }

    LaunchedEffect(viewModel.effects) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is PropertyListEffect.ShowError -> onShowMessage(effect.message)
                is PropertyListEffect.NavigateToPropertyDetail -> onNavigateToDetail(effect.propertyId)
            }
        }
    }

    val filteredProperties = state.properties.filterByQuery(query)

    // Always reveal header when list is too short to scroll or while loading
    LaunchedEffect(filteredProperties.size, state.isLoading) {
        if (state.isLoading || filteredProperties.size <= 3) {
            isHeaderVisible = true
        }
    }

    // Scroll direction → header visibility
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                val goingDown = index > prevFirstIndex ||
                    (index == prevFirstIndex && offset > prevScrollOffset + 10)
                val goingUp = index < prevFirstIndex ||
                    (index == prevFirstIndex && offset < prevScrollOffset - 10)
                when {
                    goingDown && index > 0 -> isHeaderVisible = false
                    goingUp -> isHeaderVisible = true
                }
                prevFirstIndex = index
                prevScrollOffset = offset
            }
    }

    // Paging trigger
    LaunchedEffect(
        listState,
        filteredProperties.size,
        state.isLoading,
        state.isPaging,
        state.canLoadMore,
        query,
    ) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1 }
            .collect { lastVisibleIndex ->
                val shouldLoadMore = query.isBlank() &&
                    filteredProperties.isNotEmpty() &&
                    !state.isLoading &&
                    !state.isPaging &&
                    state.canLoadMore &&
                    lastVisibleIndex >= filteredProperties.lastIndex - 2
                if (shouldLoadMore) {
                    viewModel.sendIntent(PropertyListIntent.LoadNextPage)
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = contentPadding.calculateTopPadding()),
    ) {
        AnimatedVisibility(
            visible = isHeaderVisible,
            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut(),
        ) {
            SearchHeader(
                userName = userName,
                query = query,
                onQueryChange = { query = it },
                selectedType = state.selectedType,
                onSelectType = { viewModel.sendIntent(PropertyListIntent.SelectType(it)) },
            )
        }

        when {
            state.isLoading && state.properties.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            filteredProperties.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = if (state.errorMessage != null) "No se pudo cargar" else "Sin resultados",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = state.errorMessage
                                ?: "Prueba con otro filtro o término de búsqueda",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(
                        top = 8.dp,
                        bottom = 8.dp + contentPadding.calculateBottomPadding(),
                    ),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
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
                    if (state.isPaging) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchHeader(
    userName: String,
    query: String,
    onQueryChange: (String) -> Unit,
    selectedType: PropertyType?,
    onSelectType: (PropertyType?) -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 20.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Greeting
            val greeting = if (userName.isNotBlank()) "Hola, $userName 👋" else "Explorar propiedades"
            Text(
                text = greeting,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (userName.isNotBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = "Cuba",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = {
                    Text(
                        text = "Buscar casas de playa o piscinas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                trailingIcon = if (query.isNotEmpty()) {
                    {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Rounded.Clear,
                                contentDescription = "Limpiar búsqueda",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                } else null,
                singleLine = true,
                shape = RoundedCornerShape(50),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    FilterChip(
                        selected = selectedType == null,
                        onClick = { onSelectType(null) },
                        label = { Text("Todos") },
                        shape = RoundedCornerShape(50),
                    )
                }
                items(PropertyType.entries.toList()) { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { onSelectType(if (selectedType == type) null else type) },
                        label = { Text(type.toDisplayName()) },
                        shape = RoundedCornerShape(50),
                    )
                }
            }
        }
    }
}

private fun List<Property>.filterByQuery(query: String): List<Property> {
    if (query.isBlank()) return this
    val normalized = query.trim()
    return filter { property ->
        property.title.contains(normalized, ignoreCase = true) ||
            property.address.contains(normalized, ignoreCase = true)
    }
}

private fun PropertyType.toDisplayName(): String = when (this) {
    PropertyType.BEACH_HOUSE -> "Playa"
    PropertyType.POOL -> "Piscina"
    PropertyType.APARTMENT -> "Apartamento"
    PropertyType.VILLA -> "Villa"
    PropertyType.CABIN -> "Cabaña"
    PropertyType.OTHER -> "Otros"
}
