package site.addzero.vibepocket.components.vibe

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun ComingSoonPlaceholder(sectionName: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        GlassContainer {
            Text(
                text = "$sectionName - Coming Soon",
                color = VibeColors.Secondary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
