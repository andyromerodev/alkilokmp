package dev.andyromero

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import dev.andyromero.di.initKoinIfNeeded
import dev.andyromero.navigation.AlkiloNavGraph
import dev.andyromero.navigation.Routes
import dev.andyromero.presentation.theme.AlkiloTheme
import org.koin.mp.KoinPlatform

@Composable
@Preview
fun App() {
    println("🔵 [App] composable — start")
    val koin = remember {
        println("🔵 [App] remember — initKoin")
        initKoinIfNeeded()
        println("🔵 [App] remember — getKoin")
        KoinPlatform.getKoin()
    }
    println("🔵 [App] koin ready — composing NavGraph")

    AlkiloTheme {
        AlkiloNavGraph(
            koin = koin,
            startDestination = Routes.Splash,
        )
    }
}
