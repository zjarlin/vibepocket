package site.addzero.vibepocket

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import site.addzero.component.glass.GlassColors
import site.addzero.component.glass.GlassSidebar
import site.addzero.component.glass.SidebarItem
import site.addzero.vibepocket.music.MusicVibeScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        MainScreen()
    }
}

@Composable
private fun MainScreen() {
    var selectedRoute by remember { mutableStateOf("music") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GlassColors.DarkBackground)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            GlassSidebar(
                items = sidebarItems(selectedRoute),
                onItemClick = { item -> selectedRoute = item.id },
                title = "Vibepocket"
            )

            // 简单状态驱动导航，不需要 nav3
            when (selectedRoute) {
                "music" -> MusicVibeScreen()
                "programming" -> PlaceholderScreen("💻 编程", "即将开放")
                "video" -> PlaceholderScreen("🎬 视频", "即将开放")
                "settings" -> PlaceholderScreen("⚙️ 设置", "即将开放")
                else -> MusicVibeScreen()
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(icon: String, subtitle: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GlassColors.DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            androidx.compose.material3.Text(
                text = icon,
                fontSize = 48.sp
            )
            androidx.compose.material3.Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun sidebarItems(selectedId: String): List<SidebarItem> = listOf(
    SidebarItem(id = "music", title = "音乐", icon = null, isSelected = selectedId == "music"),
    SidebarItem(id = "programming", title = "编程", icon = null, isSelected = selectedId == "programming"),
    SidebarItem(id = "video", title = "视频", icon = null, isSelected = selectedId == "video"),
    SidebarItem(id = "settings", title = "设置", icon = null, isSelected = selectedId == "settings"),
)
