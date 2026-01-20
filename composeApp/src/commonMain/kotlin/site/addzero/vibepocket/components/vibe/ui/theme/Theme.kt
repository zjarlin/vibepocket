package site.addzero.vibepocket.components.vibe.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColorScheme(
    primary = VibeColors.NeoPurple,
    secondary = VibeColors.NeoCyan,
    background = VibeColors.Background,
    surface = VibeColors.Surface,
    onPrimary = VibeColors.TextPrimary,
    onSecondary = VibeColors.TextPrimary,
    onBackground = VibeColors.TextPrimary,
    onSurface = VibeColors.TextPrimary,
)

@Composable
fun VibeMusicTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorPalette,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        content = content
    )
}
