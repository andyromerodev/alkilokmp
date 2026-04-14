package dev.andyromero.presentation.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.andyromero.presentation.property.list.components.PropertyCard

@Composable
internal fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onNavigateToDetail: (String) -> Unit,
    onShowMessage: (String) -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel.effects) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is FavoritesEffect.ShowError -> onShowMessage(effect.message)
                is FavoritesEffect.NavigateToPropertyDetail -> onNavigateToDetail(effect.propertyId)
            }
        }
    }

    val favoriteProperties = state.properties

    when {
        state.isLoading && favoriteProperties.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        favoriteProperties.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = state.errorMessage ?: "No tienes propiedades favoritas",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = contentPadding.calculateTopPadding() + 16.dp,
                    bottom = 8.dp + contentPadding.calculateBottomPadding(),
                ),
            ) {
                item {
                    Text(
                        text = "Favoritas · ${favoriteProperties.size} guardadas",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }

                items(favoriteProperties, key = { it.id }) { property ->
                    PropertyCard(
                        property = property,
                        isFavorite = true,
                        onToggleFavorite = {
                            viewModel.sendIntent(FavoritesIntent.ToggleFavorite(property.id))
                        },
                        onOpenDetail = {
                            viewModel.sendIntent(FavoritesIntent.OpenProperty(property.id))
                        },
                    )
                }
            }
        }
    }
}
