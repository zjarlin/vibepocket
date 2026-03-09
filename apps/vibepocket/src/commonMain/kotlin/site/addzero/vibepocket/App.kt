package site.addzero.vibepocket

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import site.addzero.vibepocket.navigation.DefaultMenuMetadataProvider
import site.addzero.vibepocket.navigation.DefaultScreenRouteRendererSpi
import site.addzero.vibepocket.navigation.MenuNavigationRuntimeFactory
import site.addzero.vibepocket.navigation.MenuNavigationScaffold
import site.addzero.vibepocket.navigation.MenuNavigationTheme
import site.addzero.vibepocket.navigation.NavigationThemeMode
import site.addzero.vibepocket.navigation.RouteKey
import site.addzero.vibepocket.screens.WelcomeScreenWrapper

private val WELCOME_ROUTE = RouteKey("site.addzero.vibepocket.auth.WelcomePage")

@Composable
@Preview
fun App() {
    VibepocketApp()
}

@Composable
private fun VibepocketApp() {
    val navigationRuntime = remember {
        MenuNavigationRuntimeFactory.create(
            metadataProviders = listOf(DefaultMenuMetadataProvider),
            rendererSpis = listOf(DefaultScreenRouteRendererSpi),
        )
    }
    val homeRoute = navigationRuntime.defaultRoute ?: WELCOME_ROUTE

    var isSetupDone by remember { mutableStateOf(false) }
    var themeMode by remember { mutableStateOf(NavigationThemeMode.Dark) }
    val backStack = remember { mutableStateListOf(WELCOME_ROUTE) }

    MenuNavigationTheme(themeMode = themeMode) {
        if (!isSetupDone) {
            WelcomeScreenWrapper(
                backStack = backStack,
                homeRoute = homeRoute,
                onSetupComplete = { _, _ ->
                    isSetupDone = true
                },
            )
        } else {
            MenuNavigationScaffold(
                appTitle = "Vibepocket",
                menuTree = navigationRuntime.menuTree,
                backStack = backStack,
                routeRendererRegistry = navigationRuntime.routeRendererRegistry,
                themeMode = themeMode,
                onThemeModeChange = { themeMode = it },
            )
        }
    }
}
