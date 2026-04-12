package dev.andyromero.presentation.property.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.andyromero.domain.model.Property
import dev.andyromero.presentation.components.ErrorState
import dev.andyromero.presentation.property.list.components.PropertyCard
import dev.andyromero.presentation.property.list.components.PropertyCardShimmer
import dev.andyromero.presentation.property.list.components.SearchHeader

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
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
    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(viewModel.effects) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is PropertyListEffect.ShowError -> onShowMessage(effect.message)
                is PropertyListEffect.ShowSuccess -> onShowMessage(effect.message)
                is PropertyListEffect.NavigateToPropertyDetail -> onNavigateToDetail(effect.propertyId)
            }
        }
    }

    val filteredProperties = state.properties.filterByQuery(query)

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

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .pullToRefresh(
                    isRefreshing = state.isLoading && state.properties.isEmpty(),
                    state = pullToRefreshState,
                    onRefresh = { viewModel.sendIntent(PropertyListIntent.RetryLoad) },
                ),
            contentPadding = PaddingValues(
                top = contentPadding.calculateTopPadding(),
                bottom = contentPadding.calculateBottomPadding(),
            ),
        ) {
            stickyHeader {
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
                    items(3) {
                        PropertyCardShimmer()
                    }
                }

                state.errorMessage != null && state.properties.isEmpty() -> {
                    item {
                        ErrorState(
                            message = state.errorMessage!!,
                            onRetry = { viewModel.sendIntent(PropertyListIntent.RetryLoad) },
                        )
                    }
                }

                filteredProperties.isEmpty() -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 64.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "No hay propiedades disponibles con ese filtro",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                }

                else -> {
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

        PullToRefreshDefaults.Indicator(
            state = pullToRefreshState,
            isRefreshing = state.isLoading && state.properties.isEmpty(),
            modifier = Modifier.align(Alignment.TopCenter),
        )
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
