package site.addzero.vibepocket.components.vibe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Vibe Theme Colors
object VibeColors {
    val Background = Color(0xFF0F0F1A) // Deep dark blue/black
    val Surface = Color(0xFF1E1E2E)
    val Primary = Color(0xFFB026FF) // Neon Purple
    val Secondary = Color(0xFF00E5FF) // Neon Cyan
    val Tertiary = Color(0xFFFF2E93) // Neon Pink
    val GlassBorder = Color(0x40FFFFFF)
    val GlassFill = Color(0x1AFFFFFF)
}

@Composable
fun GlassContainer(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        VibeColors.GlassFill,
                        Color(0x0DFFFFFF)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0x80FFFFFF),
                        Color(0x10FFFFFF)
                    )
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun NeonText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium
) {
    // TODO: Add proper glow effect if possible, for now just text
    Text(
        text = text,
        modifier = modifier,
        color = color,
        style = style
    )
}
