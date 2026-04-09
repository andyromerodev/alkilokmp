package dev.andyromero.presentation.host

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import dev.andyromero.navigation.Routes
import dev.andyromero.presentation.components.AlkiloBottomBar
import dev.andyromero.presentation.components.BottomNavItem

private sealed interface HostTabRoute {
    data object HostProperties : HostTabRoute
    data object HostBookings : HostTabRoute
    data object HostSettings : HostTabRoute
}

@Composable
internal fun HostTabsScreen() {
    val items = remember {
        listOf(
            BottomNavItem(label = "Propiedades", route = Routes.HostProperties, icon = Icons.Rounded.Home),
            BottomNavItem(label = "Reservas",    route = Routes.HostBookings,   icon = Icons.Rounded.BookmarkBorder),
            BottomNavItem(label = "Config",      route = Routes.HostSettings,   icon = Icons.Rounded.Settings),
        )
    }
    var tabRoute by remember { mutableStateOf<HostTabRoute>(HostTabRoute.HostProperties) }

    Scaffold(
        bottomBar = {
            AlkiloBottomBar(
                currentRoute = when (tabRoute) {
                    HostTabRoute.HostProperties -> Routes.HostProperties
                    HostTabRoute.HostBookings -> Routes.HostBookings
                    HostTabRoute.HostSettings -> Routes.HostSettings
                },
                items = items,
                onNavigate = { route ->
                    tabRoute = when (route) {
                        Routes.HostProperties -> HostTabRoute.HostProperties
                        Routes.HostBookings -> HostTabRoute.HostBookings
                        else -> HostTabRoute.HostSettings
                    }
                },
            )
        },
    ) { paddingValues ->
        when (tabRoute) {
            HostTabRoute.HostProperties -> HostTabPlaceholder(
                modifier = Modifier.padding(paddingValues),
                title = "Host Properties",
                subtitle = "Slice 2: pending migration",
            )

            HostTabRoute.HostBookings -> HostTabPlaceholder(
                modifier = Modifier.padding(paddingValues),
                title = "Host Bookings",
                subtitle = "Slice 3: pending migration",
            )

            HostTabRoute.HostSettings -> HostTabPlaceholder(
                modifier = Modifier.padding(paddingValues),
                title = "Host Settings",
                subtitle = "Pending migration",
            )
        }
    }
}

@Composable
private fun HostTabPlaceholder(
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
