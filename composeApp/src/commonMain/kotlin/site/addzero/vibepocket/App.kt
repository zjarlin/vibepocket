package site.addzero.vibepocket

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.shadcn.ui.components.sidebar.LocalSidebarState
import com.shadcn.ui.components.sidebar.SidebarLayout
import com.shadcn.ui.components.sidebar.SidebarState
import com.shadcn.ui.components.sidebar.SidebarTrigger
import com.shadcn.ui.themes.Theme
import com.shadcn.ui.themes.colorScheme
import dr.shadcn.kmp.themes.styles.ModernMinimalDark
import org.jetbrains.compose.ui.tooling.preview.Preview
import site.addzero.component.glass.GlassTheme
import site.addzero.vibepocket.auth.WelcomePage
import site.addzero.vibepocket.music.ioc.generated.iocComposables
import site.addzero.vibepocket.navigation.*

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

    Theme(style = ModernMinimalDark) {
        // A surface container using the 'background' color from the theme
        val sidebarState = remember { SidebarState() }
        CompositionLocalProvider(LocalSidebarState provides sidebarState) {
            MaterialTheme(colorScheme = MaterialTheme.colorScheme) {
                if (!isSetupDone) {
                    // 欢迎页全屏，不显示侧边栏
                    isSetupDone = WelComScreen(backStack, sunoToken, sunoBaseUrl, homeRoute, isSetupDone)
                } else {
                    // 主界面：侧边栏 + NavDisplay
                    MainScreen(menuTree, backStack)
                }
            }
        }
    }
}

@Composable
private fun MainScreen(
    menuTree: List<MenuNode>,
    backStack: SnapshotStateList<RouteKey>
) {
    val sidebarState = LocalSidebarState.current
    SidebarLayout(
        modifier = Modifier
            .fillMaxSize(),
        sidebarHeader = {
            Text(
                text = "Vibepocket",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.styles.sidebarForeground,
                modifier = Modifier.padding(start = 16.dp)
            )
        },
        sidebarContent = {
            menuTree.forEach { node ->
                MenuNodeItem(
                    node = node,
                    onLeafClick = { leaf ->
                        backStack.clear()
                        backStack.add(RouteKey(leaf.metadata.routeKey))
                    },
                    isSelected = backStack.lastOrNull()?.key == node.metadata.routeKey
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.styles.background)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SidebarTrigger()
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Vibepocket",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.styles.foreground
                )
            }
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider = { routeKey ->
                    NavEntry(routeKey) {
                        val function = iocComposables[routeKey.key]
                        function?.invoke() ?: return@NavEntry PlaceholderScreen("❓", "未知页面")
                        when (routeKey.key) {
                            "site.addzero.vibepocket.screens.ImageScreen" -> PlaceholderScreen("🖼️ 图片", "即将开放")
                            "site.addzero.vibepocket.screens.VideoScreen" -> PlaceholderScreen("🎬 视频", "即将开放")
                        }
                    }
                },
            )
        }
    }
}

@Composable
fun MenuNodeItem(node: MenuNode, onLeafClick: (MenuNode.Leaf) -> Unit, isSelected: Boolean) {
    when (node) {
        is MenuNode.Parent -> {
            Text(
                text = node.metadata.title,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.styles.sidebarForeground
            )
            node.children.forEach { child ->
                MenuNodeItem(child, onLeafClick, isSelected)
            }
        }
        is MenuNode.Leaf -> {
            com.shadcn.ui.components.sidebar.SidebarMenuButton(
                text = node.metadata.title,
                onClick = { onLeafClick(node) },
                isActive = isSelected
            )
        }
    }
}

@Composable
private fun WelComScreen(
    backStack: SnapshotStateList<RouteKey>,
    sunoToken: String,
    sunoBaseUrl: String,
    homeRoute: RouteKey,
    isSetupDone: Boolean
): Boolean {
    var sunoToken1 = sunoToken
    var sunoBaseUrl1 = sunoBaseUrl
    var isSetupDone1 = isSetupDone
    NavDisplay(
        backStack = backStack,
        onBack = {},
        entryProvider = { routeKey ->
            NavEntry(routeKey) {
                WelcomePage(
                    onEnter = { token, url ->
                        sunoToken1 = token
                        sunoBaseUrl1 = url
                        backStack.clear()
                        backStack.add(homeRoute)
                        isSetupDone1 = true
                    },
                )
            }
        },
    )
    return isSetupDone1
}

@Composable
private fun PlaceholderScreen(icon: String, subtitle: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.styles.background),
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
