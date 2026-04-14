package dev.andyromero.presentation.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.andyromero.domain.usecase.auth.ObserveCurrentProfileUseCase
import dev.andyromero.navigation.Routes
import dev.andyromero.presentation.components.AlkiloBottomBar
import dev.andyromero.presentation.components.AppSnackbarManager
import dev.andyromero.presentation.components.BottomNavItem
import dev.andyromero.presentation.favorites.FavoritesScreen
import dev.andyromero.presentation.favorites.FavoritesViewModel
import dev.andyromero.presentation.property.list.PropertyListScreen
import dev.andyromero.presentation.property.list.PropertyListViewModel
import kotlinx.coroutines.launch
import org.koin.core.Koin

@Composable
internal fun MainTabsScreen(
    koin: Koin,
    snackbarManager: AppSnackbarManager,
    currentTab: Routes,
    onTabChange: (Routes) -> Unit,
    propertyListViewModel: PropertyListViewModel,
    favoritesViewModel: FavoritesViewModel,
    onNavigateToPropertyDetail: (String) -> Unit,
) {
    val items = remember {
        listOf(
            BottomNavItem(label = "Inicio",     route = Routes.BeachList, icon = Icons.Rounded.Home),
            BottomNavItem(label = "Favoritas",  route = Routes.Favorites, icon = Icons.Rounded.Favorite),
            BottomNavItem(label = "Reservas",   route = Routes.Settings,  icon = Icons.Rounded.BookmarkBorder),
            BottomNavItem(label = "Perfil",     route = Routes.Profile,   icon = Icons.Rounded.Person),
        )
    }
    val observeProfile = remember { koin.get<ObserveCurrentProfileUseCase>() }
    val scope = rememberCoroutineScope()

    var firstName by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        observeProfile().collect { profile ->
            firstName = profile?.fullName?.split(" ")?.firstOrNull().orEmpty()
        }
    }

    val onShowMessage: (String) -> Unit = { message ->
        scope.launch {
            snackbarManager.show(text = message)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AlkiloBottomBar(
                currentRoute = currentTab,
                items = items,
                onNavigate = onTabChange,
            )
        },
    ) { paddingValues ->
        when (currentTab) {
            Routes.BeachList -> PropertyListScreen(
                viewModel = propertyListViewModel,
                onNavigateToDetail = onNavigateToPropertyDetail,
                onShowMessage = onShowMessage,
                userName = firstName,
                contentPadding = paddingValues,
            )

            Routes.Favorites -> FavoritesScreen(
                viewModel = favoritesViewModel,
                onNavigateToDetail = onNavigateToPropertyDetail,
                onShowMessage = onShowMessage,
                contentPadding = paddingValues,
            )

            else -> TabPlaceholder(modifier = Modifier.padding(paddingValues))
        }
    }
}

@Composable
private fun TabPlaceholder(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Próximamente", style = MaterialTheme.typography.headlineMedium)
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = "Slice pendiente",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
