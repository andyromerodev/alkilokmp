package dev.andyromero.navigation

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.andyromero.presentation.admin.bookings.AdminBookingsScreen
import dev.andyromero.presentation.auth.login.LoginScreen
import dev.andyromero.presentation.auth.login.LoginViewModel
import dev.andyromero.presentation.auth.register.RegisterScreen
import dev.andyromero.presentation.auth.register.RegisterViewModel
import dev.andyromero.presentation.booking.create.CreateBookingScreen
import dev.andyromero.domain.model.UserRole
import dev.andyromero.domain.usecase.auth.ObserveAuthStateUseCase
import dev.andyromero.domain.usecase.auth.ObserveCurrentProfileUseCase
import dev.andyromero.domain.usecase.auth.RestoreSessionUseCase
import dev.andyromero.presentation.host.HostTabsScreen
import dev.andyromero.presentation.main.MainTabsScreen
import dev.andyromero.presentation.property.detail.PropertyDetailScreen
import kotlinx.coroutines.flow.map
import org.koin.core.Koin
import org.koin.core.parameter.parametersOf

@Composable
internal fun AlkiloNavGraph(
    koin: Koin,
    startDestination: Routes = Routes.Splash,
) {
    var route by remember { mutableStateOf<Routes>(startDestination) }

    val restoreSessionUseCase = remember { koin.get<RestoreSessionUseCase>() }
    val observeAuthStateUseCase = remember { koin.get<ObserveAuthStateUseCase>() }
    val observeCurrentProfileUseCase = remember { koin.get<ObserveCurrentProfileUseCase>() }
    val isLoggedIn by observeAuthStateUseCase()
        .map<Boolean, Boolean?> { it }
        .collectAsState(initial = null)
    val currentProfile by observeCurrentProfileUseCase().collectAsState(initial = null)

    LaunchedEffect(Unit) {
        restoreSessionUseCase()
    }

    LaunchedEffect(route, isLoggedIn, currentProfile) {
        if (route != Routes.Splash) return@LaunchedEffect

        when (isLoggedIn) {
            null -> Unit
            false -> route = Routes.Login()
            true -> {
                route = when (currentProfile?.role ?: UserRole.CLIENT) {
                    UserRole.CLIENT -> Routes.MainTabs
                    UserRole.HOST -> Routes.HostTabs
                    UserRole.ADMIN -> Routes.AdminBookings
                }
            }
        }
    }

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
                onNavigateToHostTabs = {
                    route = Routes.HostTabs
                },
                onNavigateToAdminBookings = {
                    route = Routes.AdminBookings
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
            CreateBookingScreen(
                propertyId = currentRoute.propertyId,
                onNavigateBack = {
                    route = Routes.Login(returnPropertyId = currentRoute.propertyId)
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
                onNavigateToPropertyDetail = { propertyId ->
                    route = Routes.PropertyDetail(propertyId)
                },
            )
        }

        is Routes.PropertyDetail -> {
            val currentRoute = route as Routes.PropertyDetail
            PropertyDetailScreen(
                propertyId = currentRoute.propertyId,
                onNavigateBack = {
                    route = Routes.MainTabs
                },
                onNavigateToBooking = { propertyId ->
                    route = Routes.CreateBooking(propertyId)
                },
            )
        }

        Routes.HostTabs -> {
            HostTabsScreen()
        }

        Routes.HostProperties,
        Routes.HostBookings,
        Routes.HostSettings,
        is Routes.HostPropertyDetail,
        is Routes.HostEditProperty,
        is Routes.HostCreateBooking,
        is Routes.HostBookingDetail -> {
            HostTabsScreen()
        }

        Routes.AdminBookings -> {
            AdminBookingsScreen(
                onNavigateToLogin = {
                    route = Routes.Login()
                },
            )
        }
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
