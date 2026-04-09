package dev.andyromero.presentation.components

import androidx.compose.ui.graphics.vector.ImageVector
import dev.andyromero.navigation.Routes

internal data class BottomNavItem(
    val label: String,
    val route: Routes,
    val icon: ImageVector,
)
