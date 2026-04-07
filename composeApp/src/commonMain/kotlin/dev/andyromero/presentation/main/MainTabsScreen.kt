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

@Composable
internal fun MainTabsScreen() {
    val items = remember {
        listOf(
            BottomNavItem(label = "Playa", route = Routes.BeachList),
            BottomNavItem(label = "Favoritas", route = Routes.Favorites),
            BottomNavItem(label = "Settings", route = Routes.Settings),
        )
    }
    var tabRoute by remember { mutableStateOf<Routes>(Routes.BeachList) }

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
            Routes.BeachList -> TabPlaceholderScreen(
                modifier = Modifier.padding(paddingValues),
                title = "Property List",
                subtitle = "Main tabs / beach list",
            )

            Routes.Favorites -> TabPlaceholderScreen(
                modifier = Modifier.padding(paddingValues),
                title = "Favoritas",
                subtitle = "Slice 2: pending migration",
            )

            Routes.Settings -> TabPlaceholderScreen(
                modifier = Modifier.padding(paddingValues),
                title = "Settings",
                subtitle = "Slice 1 done, settings pending",
            )

            else -> TabPlaceholderScreen(
                modifier = Modifier.padding(paddingValues),
                title = "Main",
                subtitle = "Unknown tab",
            )
        }
    }
}

@Composable
private fun TabPlaceholderScreen(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
