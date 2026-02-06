package site.addzero.vibepocket.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import site.addzero.component.glass.GlassColors

@Composable
fun MusicScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GlassColors.DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🎵 Music",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ProgrammingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GlassColors.DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "💻 Programming",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun VideoScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GlassColors.DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🎬 Video",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SettingsScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GlassColors.DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "⚙️ Settings",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
