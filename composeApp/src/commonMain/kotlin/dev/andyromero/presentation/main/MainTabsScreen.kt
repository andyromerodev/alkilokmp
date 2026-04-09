package dev.andyromero.presentation.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.andyromero.navigation.Routes
import dev.andyromero.presentation.components.AlkiloBottomBar
import dev.andyromero.presentation.components.BottomNavItem
import dev.andyromero.presentation.favorites.FavoritesScreen
import dev.andyromero.presentation.favorites.FavoritesViewModel
import dev.andyromero.presentation.property.list.PropertyListScreen
import dev.andyromero.presentation.property.list.PropertyListViewModel
import org.koin.core.Koin

@Composable
internal fun MainTabsScreen(
    koin: Koin,
    onNavigateToPropertyDetail: (String) -> Unit,
) {
    val items = remember {
        listOf(
            BottomNavItem(label = "Playa", route = Routes.BeachList),
            BottomNavItem(label = "Favoritas", route = Routes.Favorites),
            BottomNavItem(label = "Settings", route = Routes.Settings),
        )
    }
    var tabRoute by remember { mutableStateOf<Routes>(Routes.BeachList) }

    val propertyListViewModel = remember { koin.get<PropertyListViewModel>() }
    val favoritesViewModel = remember { koin.get<FavoritesViewModel>() }

    Scaffold(
        bottomBar = {
            AlkiloBottomBar(
                currentRoute = tabRoute,
                items = items,
                onNavigate = { tabRoute = it },
            )
        },
    ) { paddingValues ->
        when (tabRoute) {
            Routes.BeachList -> PropertyListScreen(
                viewModel = propertyListViewModel,
                onNavigateToDetail = onNavigateToPropertyDetail,
                onShowMessage = { },
            )

            Routes.Favorites -> FavoritesScreen(
                viewModel = favoritesViewModel,
                onNavigateToDetail = onNavigateToPropertyDetail,
                onShowMessage = { },
            )

            Routes.Settings -> SettingsTabPlaceholder(
                modifier = Modifier.padding(paddingValues),
            )

            else -> SettingsTabPlaceholder(
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}

@Composable
private fun SettingsTabPlaceholder(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Settings", style = MaterialTheme.typography.headlineMedium)
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = "Slice pendiente",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
