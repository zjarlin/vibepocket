package site.addzero.vibepocket

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import site.addzero.component.glass.GlassColors
import site.addzero.component.glass.GlassSidebar
import site.addzero.component.glass.SidebarItem
import site.addzero.vibepocket.screens.MusicScreen
import site.addzero.vibepocket.screens.ProgrammingScreen
import site.addzero.vibepocket.screens.SettingsScreen
import site.addzero.vibepocket.screens.VideoScreen

// 自定义图标（因为 Material Icons 在 Compose Multiplatform 中的导入可能有问题）
// 可以稍后替换为自定义图标或其他图标库

@Composable
@Preview
fun App() {
    MaterialTheme {
        val backStack = remember { mutableStateListOf("music") }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GlassColors.DarkBackground)
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                // 侧边栏
                val sidebarItems = listOf(
                    SidebarItem(
                        id = "music",
                        title = "音乐",
                        icon = null,
                        isSelected = backStack.lastOrNull() == "music"
                    ),
                    SidebarItem(
                        id = "programming",
                        title = "编程",
                        icon = null,
                        isSelected = backStack.lastOrNull() == "programming"
                    ),
                    SidebarItem(
                        id = "video",
                        title = "视频",
                        icon = null,
                        isSelected = backStack.lastOrNull() == "video"
                    ),
                    SidebarItem(
                        id = "settings",
                        title = "设置",
                        icon = null,
                        isSelected = backStack.lastOrNull() == "settings"
                    )
                )

                GlassSidebar(
                    items = sidebarItems,
                    onItemClick = { item ->
                        backStack.clear()
                        backStack.add(item.id)
                    },
                    title = "Vibepocket"
                )

                // 内容区域
                NavDisplay(backStack) { route ->
                    when (route) {
                        "music" -> NavEntry(route) { MusicScreen() }
                        "programming" -> NavEntry(route) { ProgrammingScreen() }
                        "video" -> NavEntry(route) { VideoScreen() }
                        "settings" -> NavEntry(route) { SettingsScreen() }
                        else -> NavEntry(route) { MusicScreen() }
                    }
                }
            }
        }
    }
}
