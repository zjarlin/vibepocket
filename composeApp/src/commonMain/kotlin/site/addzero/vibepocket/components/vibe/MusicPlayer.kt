package site.addzero.vibepocket.components.vibe


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

@Composable
fun MusicPlayer(modifier: Modifier = Modifier) {
    GlassContainer(
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            Text(
                "Now Playing",
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Visualization Area (Fake Waveform)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                FakeWaveform(color = VibeColors.Primary, modifier = Modifier.fillMaxSize())
                FakeWaveform(color = VibeColors.Secondary, modifier = Modifier.fillMaxSize(), offset = 50f)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Player Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {}) { Icon(Icons.Default.Shuffle, "Shuffle", tint = VibeColors.Secondary) }
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(onClick = {}) { Icon(Icons.Default.SkipPrevious, "Previous", tint = Color.White) }
                Spacer(modifier = Modifier.width(16.dp))
                
                // Play Button
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            brush = Brush.linearGradient(listOf(VibeColors.Primary, VibeColors.Secondary)),
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PlayArrow, "Play", tint = Color.White, modifier = Modifier.size(32.dp))
                }

                Spacer(modifier = Modifier.width(16.dp))
                IconButton(onClick = {}) { Icon(Icons.Default.SkipNext, "Next", tint = Color.White) }
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(onClick = {}) { Icon(Icons.Default.Repeat, "Repeat", tint = VibeColors.Secondary) }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("1:24", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                Slider(
                    value = 0.3f,
                    onValueChange = {},
                    modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = VibeColors.Secondary,
                        activeTrackColor = VibeColors.Primary,
                        inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                    )
                )
                Text("4:55", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Track Info
            Row(verticalAlignment = Alignment.CenterVertically) {
               // Album Art Placeholder
               Box(
                   modifier = Modifier
                       .size(60.dp)
                       .clip(RoundedCornerShape(8.dp))
                       .background(VibeColors.Tertiary.copy(alpha=0.5f))
                       .border(1.dp, VibeColors.Secondary, RoundedCornerShape(8.dp))
               )
               Spacer(modifier = Modifier.width(16.dp))
               Column {
                   Text("Midnight Drift", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                   Text("Artist: Neon Dreams", color = Color.White.copy(alpha=0.7f), fontSize = 14.sp)
                   Text("Album: Ethereal Beats", color = Color.White.copy(alpha=0.5f), fontSize = 12.sp)
               }
            }
        }
    }
}

@Composable
fun FakeWaveform(color: Color, modifier: Modifier = Modifier, offset: Float = 0f) {
    Canvas(modifier = modifier) {
        val path = Path()
        val width = size.width
        val height = size.height
        val midY = height / 2

        path.moveTo(0f, midY)
        
        val points = 20
        val segmentWidth = width / points
        
        for (i in 0..points) {
            val x = i * segmentWidth
            // Generate a random-ish wave
            val amplitude = if (i % 2 == 0) 50f else -50f
            val y = midY + (amplitude * (0.5f + Random.nextFloat()))
            
            // Smooth curve
            if (i == 0) {
                path.lineTo(x, y)
            } else {
                val prevX = (i - 1) * segmentWidth
                val controlX1 = prevX + segmentWidth / 2
                val controlX2 = x - segmentWidth / 2
                path.cubicTo(controlX1, midY + offset, controlX2, y, x, y)
            }
        }
        
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
            alpha = 0.7f
        )
    }
}
