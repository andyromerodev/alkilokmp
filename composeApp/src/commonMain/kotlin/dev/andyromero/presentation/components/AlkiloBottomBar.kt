package dev.andyromero.presentation.components

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import dev.andyromero.navigation.Routes

@Composable
internal fun AlkiloBottomBar(
    currentRoute: Routes,
    items: List<BottomNavItem>,
    onNavigate: (Routes) -> Unit,
) {
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = { Text(item.label.take(1)) },
                label = { Text(item.label) },
            )
        }
    }
}
