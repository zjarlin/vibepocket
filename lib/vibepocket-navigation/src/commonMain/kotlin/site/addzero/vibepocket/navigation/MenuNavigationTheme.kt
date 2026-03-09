package site.addzero.vibepocket.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.shadcn.ui.themes.ShadcnTheme

enum class NavigationThemeMode {
    Light,
    Dark,
    ;

    fun toggled(): NavigationThemeMode {
        return if (this == Dark) Light else Dark
    }
}

@Immutable
data class NavigationChromeColors(
    val windowGradientStart: Color,
    val windowGradientEnd: Color,
    val sidebarSurface: Color,
    val sidebarBorder: Color,
    val headerSurface: Color,
    val headerBorder: Color,
    val contentSurface: Color,
    val contentBorder: Color,
    val selectedItemContainer: Color,
)

private val LightMaterialColors: ColorScheme = lightColorScheme(
    primary = Color(0xFF2F66F6),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF2F8CFF),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFF4F7FB),
    onBackground = Color(0xFF1D2735),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1D2735),
    surfaceVariant = Color(0xFFE7EDF7),
    onSurfaceVariant = Color(0xFF566476),
    outline = Color(0xFFD0D8E5),
)

private val DarkMaterialColors: ColorScheme = darkColorScheme(
    primary = Color(0xFF8FAAFF),
    onPrimary = Color(0xFF091229),
    secondary = Color(0xFF59D4FF),
    onSecondary = Color(0xFF08212B),
    background = Color(0xFF09101F),
    onBackground = Color(0xFFF3F7FF),
    surface = Color(0xFF111A2C),
    onSurface = Color(0xFFF3F7FF),
    surfaceVariant = Color(0xFF1C2840),
    onSurfaceVariant = Color(0xFFB8C4D9),
    outline = Color(0xFF33425E),
)

private val LightChromeColors = NavigationChromeColors(
    windowGradientStart = Color(0xFFF7F9FD),
    windowGradientEnd = Color(0xFFE4EEFF),
    sidebarSurface = Color(0xEFFFFFFF),
    sidebarBorder = Color(0xFFD7E0EC),
    headerSurface = Color(0xCCFFFFFF),
    headerBorder = Color(0xFFD7E0EC),
    contentSurface = Color(0xF7FFFFFF),
    contentBorder = Color(0xFFD7E0EC),
    selectedItemContainer = Color(0x1F2F66F6),
)

private val DarkChromeColors = NavigationChromeColors(
    windowGradientStart = Color(0xFF08101F),
    windowGradientEnd = Color(0xFF19294A),
    sidebarSurface = Color(0xCC10192A),
    sidebarBorder = Color(0xFF2A3A59),
    headerSurface = Color(0xCC10192A),
    headerBorder = Color(0xFF2A3A59),
    contentSurface = Color(0xD9152136),
    contentBorder = Color(0xFF2A3A59),
    selectedItemContainer = Color(0x338FAAFF),
)

private val LocalNavigationChromeColors = staticCompositionLocalOf {
    DarkChromeColors
}

object NavigationChromeTheme {
    val colors: NavigationChromeColors
        @Composable
        @ReadOnlyComposable
        get() = LocalNavigationChromeColors.current
}

@Composable
fun MenuNavigationTheme(
    themeMode: NavigationThemeMode,
    content: @Composable () -> Unit,
) {
    val isDarkTheme = themeMode == NavigationThemeMode.Dark
    val chromeColors = if (isDarkTheme) DarkChromeColors else LightChromeColors

    ShadcnTheme(
        isDarkTheme = isDarkTheme,
        materialLightColors = LightMaterialColors,
        materialDarkColors = DarkMaterialColors,
    ) {
        CompositionLocalProvider(
            LocalNavigationChromeColors provides chromeColors,
            content = content,
        )
    }
}
