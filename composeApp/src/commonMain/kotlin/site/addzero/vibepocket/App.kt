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
import site.addzero.component.glass.GlassTheme
import site.addzero.vibepocket.music.MusicVibeScreen
import site.addzero.vibepocket.navigation.MenuNodeSidebar
import site.addzero.vibepocket.navigation.MenuTreeBuilder
import site.addzero.vibepocket.navigation.defaultMenuItems
import site.addzero.vibepocket.settings.ConfigStore
import site.addzero.vibepocket.settings.SettingsPage
import site.addzero.vibepocket.settings.getPlatformConfigPath

@Composable
@Preview
fun App() {

    // 从默认菜单元数据构建菜单树
    val menuTree = remember { MenuTreeBuilder.buildTree(defaultMenuItems) }
    // 扁平化为可见叶节点列表，用于确定默认路由
    val visibleLeaves = remember { MenuTreeBuilder.flattenVisibleLeaves(menuTree) }
    // 当前选中的路由 key（全限定名），默认选中第一个可见叶节点
    var selectedRouteKey by remember { mutableStateOf(visibleLeaves.firstOrNull()?.routeKey ?: "") }
    // 配置持久化存储
    val configStore = remember { ConfigStore(getPlatformConfigPath()) }

    MaterialTheme {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(GlassTheme.DarkBackground)
        ) {
            // 菜单元数据驱动的侧边栏
            MenuNodeSidebar(
                menuTree = menuTree,
                selectedRouteKey = selectedRouteKey,
                onLeafClick = { node -> selectedRouteKey = node.metadata.routeKey },
                title = "Vibepocket",
            )

            // 路由分发：根据 routeKey 全限定名匹配对应页面
            when (selectedRouteKey) {
                "site.addzero.vibepocket.music.MusicVibeScreen" -> MusicVibeScreen(configStore)
                "site.addzero.vibepocket.screens.ImageScreen" -> PlaceholderScreen("🖼️ 图片", "即将开放")
                "site.addzero.vibepocket.screens.VideoScreen" -> PlaceholderScreen("🎬 视频", "即将开放")
                "site.addzero.vibepocket.settings.SettingsPage" -> SettingsPage(configStore)
                else -> {
                    // 未匹配时回退到第一个可见叶节点
                    val fallbackRouteKey = visibleLeaves.firstOrNull()?.routeKey
                    if (fallbackRouteKey != null && fallbackRouteKey != selectedRouteKey) {
                        LaunchedEffect(Unit) {
                            selectedRouteKey = fallbackRouteKey
                        }
                    }
                    MusicVibeScreen(configStore)
                }
            }
        }
    }
}

/**
 * 占位页面 — 用于尚未实现的功能模块。
 *
 * @param icon 模块图标（emoji）
 * @param subtitle 占位提示文字
 */
@Composable
private fun PlaceholderScreen(icon: String, subtitle: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GlassTheme.DarkBackground),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = icon,
                fontSize = 48.sp,
            )
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
