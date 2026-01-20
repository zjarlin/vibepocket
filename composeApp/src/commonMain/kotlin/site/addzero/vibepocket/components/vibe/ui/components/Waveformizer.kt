package site.addzero.vibepocket.components.vibe.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import site.addzero.vibepocket.components.vibe.ui.theme.GradientPrimary
import site.addzero.vibepocket.components.vibe.ui.theme.GradientSecondary
import kotlin.math.sin

@Composable
fun WaveformVisualizer(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition()
    val phase = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val amplitudeAnim = infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val centerY = height / 2

        // Draw multiple waves for depth

        // Back wave
        drawPath(
            path = createSineWavePath(width, centerY, 50f, phase.value, 0.8f),
            brush = Brush.horizontalGradient(GradientSecondary),
            style = Stroke(width = 4f),
            alpha = 0.5f
        )

        // Front wave (Primary)
        drawPath(
            path = createSineWavePath(width, centerY, 80f * (if(isPlaying) amplitudeAnim.value else 0.2f), phase.value * 1.5f, 1.2f),
            brush = Brush.horizontalGradient(GradientPrimary),
            style = Stroke(width = 6f, cap = StrokeCap.Round)
        )
    }
}

private fun createSineWavePath(
    width: Float,
    centerY: Float,
    amplitude: Float,
    phase: Float,
    frequency: Float
): Path {
    val path = Path()
    path.moveTo(0f, centerY)

    val points = 100
    for (i in 0..points) {
        val x = (i.toFloat() / points) * width
        // Combine two sine waves for more interesting shape
        val normalizedX = (i.toFloat() / points) * 2 * Math.PI.toFloat() * frequency
        val y = centerY + sin(normalizedX + phase) * amplitude

        path.lineTo(x, y)
    }

    return path
}
