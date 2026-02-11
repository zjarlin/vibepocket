package site.addzero.vibepocket

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import site.addzero.component.glass.GlassTheme
import site.addzero.vibepocket.auth.WelcomePage
import site.addzero.vibepocket.music.GadulkaPlayerDemo
import site.addzero.vibepocket.music.MusicVibeScreen
import site.addzero.vibepocket.navigation.MenuNodeSidebar
import site.addzero.vibepocket.navigation.MenuTreeBuilder
import site.addzero.vibepocket.navigation.RouteKey
import site.addzero.vibepocket.navigation.defaultMenuItems
import site.addzero.vibepocket.settings.SettingsPage

private val WELCOME_ROUTE = RouteKey("site.addzero.vibepocket.auth.WelcomePage")

@Composable
@Preview
fun App() {
    val menuTree = remember { MenuTreeBuilder.buildTree(defaultMenuItems) }
    val visibleLeaves = remember { MenuTreeBuilder.flattenVisibleLeaves(menuTree) }
    val homeRoute = RouteKey(visibleLeaves.firstOrNull()?.routeKey ?: "")

    var isSetupDone by remember { mutableStateOf(false) }
    val backStack = remember { mutableStateListOf(WELCOME_ROUTE) }

    // API 配置（欢迎页填写后传入，后续会改成从 DB 读取）
    var sunoToken by remember { mutableStateOf("") }
    var sunoBaseUrl by remember { mutableStateOf("https://api.sunoapi.org/api/v1") }

    MaterialTheme {
        if (!isSetupDone) {
            // 欢迎页全屏，不显示侧边栏
            NavDisplay(
                backStack = backStack,
                onBack = {},
                entryProvider = { routeKey ->
                    NavEntry(routeKey) {
                        WelcomePage(
                            onEnter = { token, url ->
                                sunoToken = token
                                sunoBaseUrl = url
                                backStack.clear()
                                backStack.add(homeRoute)
                                isSetupDone = true
                            },
                        )
                    }
                },
            )
        } else {
            // 主界面：侧边栏 + NavDisplay
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GlassTheme.DarkBackground)
            ) {
                MenuNodeSidebar(
                    menuTree = menuTree,
                    selectedRouteKey = backStack.lastOrNull()?.key ?: "",
                    onLeafClick = { node ->
                        backStack.clear()
                        backStack.add(RouteKey(node.metadata.routeKey))
                    },
                    title = "Vibepocket",
                )

                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryProvider = { routeKey ->
                        NavEntry(routeKey) {
                            when (routeKey.key) {
                                "site.addzero.vibepocket.music.MusicVibeScreen" -> MusicVibeScreen()
                                "site.addzero.vibepocket.music.GadulkaPlayerDemo" -> GadulkaPlayerDemo()
                                "site.addzero.vibepocket.screens.ImageScreen" -> PlaceholderScreen("🖼️ 图片", "即将开放")
                                "site.addzero.vibepocket.screens.VideoScreen" -> PlaceholderScreen("🎬 视频", "即将开放")
                                "site.addzero.vibepocket.settings.SettingsPage" -> SettingsPage()
                                else -> PlaceholderScreen("❓", "未知页面")
                            }
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(icon: String, subtitle: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GlassTheme.DarkBackground),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = icon, fontSize = 48.sp)
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
