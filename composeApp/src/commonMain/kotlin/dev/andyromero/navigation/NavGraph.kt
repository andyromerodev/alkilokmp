package dev.andyromero.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.andyromero.presentation.auth.login.LoginScreen
import dev.andyromero.presentation.auth.login.LoginViewModel
import dev.andyromero.presentation.auth.register.RegisterScreen
import dev.andyromero.presentation.auth.register.RegisterViewModel
import dev.andyromero.presentation.booking.create.CreateBookingScreen
import dev.andyromero.presentation.booking.create.CreateBookingViewModel
import dev.andyromero.domain.usecase.auth.ObserveAuthStateUseCase
import dev.andyromero.domain.usecase.auth.RestoreSessionUseCase
import dev.andyromero.presentation.components.AppSnackbarHost
import dev.andyromero.presentation.components.rememberAppSnackbarManager
import dev.andyromero.presentation.favorites.FavoritesViewModel
import dev.andyromero.presentation.main.MainTabsScreen
import dev.andyromero.presentation.property.detail.PropertyDetailScreen
import dev.andyromero.presentation.property.detail.PropertyDetailViewModel
import dev.andyromero.presentation.property.list.PropertyListViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.Koin
import org.koin.core.parameter.parametersOf

@Composable
internal fun AlkiloNavGraph(
    koin: Koin,
    startDestination: Routes = Routes.Splash,
) {
    println("🔵 [Nav] AlkiloNavGraph — start, route=$startDestination")
    var route by remember { mutableStateOf<Routes>(startDestination) }
    var mainTabRoute by remember { mutableStateOf<Routes>(Routes.BeachList) }

    val restoreSessionUseCase = remember { koin.get<RestoreSessionUseCase>() }
    val observeAuthStateUseCase = remember { koin.get<ObserveAuthStateUseCase>() }
    val propertyListViewModel = remember { koin.get<PropertyListViewModel>() }
    val favoritesViewModel = remember { koin.get<FavoritesViewModel>() }
    val isLoggedIn by observeAuthStateUseCase()
        .map<Boolean, Boolean?> { it }
        .collectAsState(initial = null)

    LaunchedEffect(Unit) {
        restoreSessionUseCase()
    }

    LaunchedEffect(route, isLoggedIn) {
        println("🔵 [Nav] splash check — route=$route isLoggedIn=$isLoggedIn")
        if (route != Routes.Splash) return@LaunchedEffect

        when (isLoggedIn) {
            null -> Unit
            false -> {
                println("🔵 [Nav] → Login")
                route = Routes.Login()
            }
            true -> {
                println("🔵 [Nav] → MainTabs")
                route = Routes.MainTabs
            }
        }
    }

    val snackbarManager = rememberAppSnackbarManager()
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        when (route) {
            Routes.Splash -> SplashScreen()

            is Routes.Login -> {
                val currentRoute = route as Routes.Login
                val loginViewModel = remember(currentRoute.returnPropertyId) {
                    koin.get<LoginViewModel> {
                        parametersOf(currentRoute.returnPropertyId)
                    }
                }

                LoginScreen(
                    viewModel = loginViewModel,
                    onNavigateToRegister = { returnId ->
                        route = Routes.Register(returnId)
                    },
                    onNavigateToBooking = { propertyId ->
                        route = Routes.CreateBooking(propertyId)
                    },
                    onNavigateToPropertyList = {
                        route = Routes.MainTabs
                    },
                )
            }

            is Routes.Register -> {
                val currentRoute = route as Routes.Register
                val registerViewModel = remember(currentRoute.returnPropertyId) {
                    koin.get<RegisterViewModel> {
                        parametersOf(currentRoute.returnPropertyId)
                    }
                }

                RegisterScreen(
                    viewModel = registerViewModel,
                    onNavigateToLogin = { returnId ->
                        route = Routes.Login(returnId)
                    },
                    onNavigateToBooking = { propertyId ->
                        route = Routes.CreateBooking(propertyId)
                    },
                    onNavigateToPropertyList = {
                        route = Routes.MainTabs
                    },
                )
            }

            is Routes.CreateBooking -> {
                val currentRoute = route as Routes.CreateBooking
                val createBookingViewModel = remember(currentRoute.propertyId) {
                    koin.get<CreateBookingViewModel> {
                        parametersOf(currentRoute.propertyId)
                    }
                }
                CreateBookingScreen(
                    viewModel = createBookingViewModel,
                    onNavigateBack = {
                        route = if (currentRoute.returnToPropertyDetail) {
                            Routes.PropertyDetail(currentRoute.propertyId)
                        } else {
                            Routes.Login(returnPropertyId = currentRoute.propertyId)
                        }
                    },
                )
            }

            Routes.MainTabs,
            Routes.PropertyList,
            Routes.BeachList,
            Routes.Favorites,
            Routes.Settings,
            Routes.Profile -> {
                MainTabsScreen(
                    koin = koin,
                    snackbarManager = snackbarManager,
                    currentTab = mainTabRoute,
                    onTabChange = { mainTabRoute = it },
                    propertyListViewModel = propertyListViewModel,
                    favoritesViewModel = favoritesViewModel,
                    onNavigateToPropertyDetail = { propertyId ->
                        route = Routes.PropertyDetail(propertyId)
                    },
                )
            }

            is Routes.PropertyDetail -> {
                val currentRoute = route as Routes.PropertyDetail
                val propertyDetailViewModel = remember(currentRoute.propertyId) {
                    koin.get<PropertyDetailViewModel> {
                        parametersOf(currentRoute.propertyId)
                    }
                }
                PropertyDetailScreen(
                    viewModel = propertyDetailViewModel,
                    onNavigateBack = {
                        route = Routes.MainTabs
                    },
                    onNavigateToBooking = { propertyId ->
                        route = Routes.CreateBooking(propertyId, returnToPropertyDetail = true)
                    },
                    onShowMessage = { message ->
                        scope.launch {
                            snackbarManager.show(text = message)
                        }
                    },
                )
            }


        }

        AppSnackbarHost(
            snackbarManager = snackbarManager,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun SplashScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Cargando sesión...",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
