package site.addzero.vibepocket.components.vibe.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import site.addzero.vibepocket.components.vibe.ui.theme.VibeColors

/**
 * A container that simulates a glassmorphism effect.
 * Note: Real-time background blur (backdrop filter) requires platform-specific implementations
 * or newer Compose APIs (Modifier.blur is content blur, not background).
 * Here we simulate it with semi-transparent surfaces and gradient borders.
 */
@Composable
fun GlassySurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    backgroundColor: Color = VibeColors.SurfaceGlass,
    borderColor: Color = Color.White.copy(alpha = 0.2f),
    borderWidth: Dp = 1.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(borderWidth, borderColor, shape)
    ) {
        content()
    }
}

@Composable
fun NeonCard(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    GlassySurface(
        modifier = modifier,
        borderColor = Color.White.copy(alpha = 0.1f)
    ) {
        // Add a subtle inner glow or gradient overlay if needed
        content()
    }
}
