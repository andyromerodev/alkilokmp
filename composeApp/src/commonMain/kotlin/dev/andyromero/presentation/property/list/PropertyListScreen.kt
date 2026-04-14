package dev.andyromero.presentation.property.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import dev.andyromero.presentation.components.ErrorState
import dev.andyromero.presentation.property.list.components.PropertyCard
import dev.andyromero.presentation.property.list.components.PropertyCardShimmer
import dev.andyromero.presentation.property.list.components.SearchHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PropertyListScreen(
    viewModel: PropertyListViewModel,
    onNavigateToDetail: (String) -> Unit,
    onShowMessage: (String) -> Unit = {},
    userName: String = "",
    contentPadding: PaddingValues = PaddingValues(),
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = state.firstVisibleItemIndex,
        initialFirstVisibleItemScrollOffset = state.firstVisibleItemScrollOffset,
    )
    val pullToRefreshState = rememberPullToRefreshState()
    val density = LocalDensity.current
    var headerHeightPx by remember { mutableStateOf(0) }
    val headerHeight by remember { derivedStateOf { with(density) { headerHeightPx.toDp() } } }

    LaunchedEffect(viewModel.effects) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is PropertyListEffect.ShowError -> onShowMessage(effect.message)
                is PropertyListEffect.ShowSuccess -> onShowMessage(effect.message)
                is PropertyListEffect.NavigateToPropertyDetail -> onNavigateToDetail(effect.propertyId)
            }
        }
    }

    val properties = state.properties

    val isHeaderVisible by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset < 10
        }
    }

    // Paging trigger
    LaunchedEffect(
        listState,
        properties.size,
        state.isLoading,
        state.isPaging,
        state.canLoadMore,
        state.searchQuery,
    ) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1 }
            .collect { lastVisibleIndex ->
                val shouldLoadMore = state.searchQuery.isBlank() &&
                    properties.isNotEmpty() &&
                    !state.isLoading &&
                    !state.isPaging &&
                    state.canLoadMore &&
                    lastVisibleIndex >= properties.lastIndex - 2
                if (shouldLoadMore) {
                    viewModel.sendIntent(PropertyListIntent.LoadNextPage)
                }
            }
    }

    DisposableEffect(listState) {
        onDispose {
            viewModel.sendIntent(
                PropertyListIntent.SaveScrollPosition(
                    index = listState.firstVisibleItemIndex,
                    offset = listState.firstVisibleItemScrollOffset,
                )
            )
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
                top = contentPadding.calculateTopPadding() + headerHeight,
                bottom = contentPadding.calculateBottomPadding(),
            ),
        ) {
            when {
                state.isLoading && state.properties.isEmpty() -> {
                    items(3) { PropertyCardShimmer() }
                }

                state.errorMessage != null && state.properties.isEmpty() -> {
                    item {
                        ErrorState(
                            message = state.errorMessage!!,
                            onRetry = { viewModel.sendIntent(PropertyListIntent.RetryLoad) },
                        )
                    }
                }

                properties.isEmpty() -> {
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
                    items(properties, key = { it.id }) { property ->
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

        AnimatedVisibility(
            visible = isHeaderVisible,
            enter = slideInVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium,
                ),
            ) { -it } + fadeIn(tween(200)),
            exit = slideOutVertically(tween(180)) { -it } + fadeOut(tween(150)),
            modifier = Modifier.align(Alignment.TopStart),
        ) {
            SearchHeader(
                userName = userName,
                query = state.searchQuery,
                onQueryChange = { viewModel.sendIntent(PropertyListIntent.UpdateSearchQuery(it)) },
                selectedType = state.selectedType,
                onSelectType = { viewModel.sendIntent(PropertyListIntent.SelectType(it)) },
                modifier = Modifier.onSizeChanged { headerHeightPx = it.height },
            )
        }

        AnimatedVisibility(
            visible = !isHeaderVisible,
            enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(tween(200)),
            exit = scaleOut(tween(150)) + fadeOut(tween(150)),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = 16.dp,
                    bottom = contentPadding.calculateBottomPadding() + 16.dp,
                ),
        ) {
            FloatingActionButton(
                onClick = { scope.launch { listState.animateScrollToItem(0) } },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Ir al inicio",
                )
            }
        }

        PullToRefreshDefaults.Indicator(
            state = pullToRefreshState,
            isRefreshing = state.isLoading && state.properties.isEmpty(),
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}

