package site.addzero.vibepocket

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.shadcn.ui.themes.styles
import dr.shadcn.kmp.themes.styles.ModernMinimalDark
import site.addzero.vibepocket.auth.WelcomePage
import site.addzero.vibepocket.music.ioc.generated.iocComposablesByTag
import site.addzero.vibepocket.navigation.*
import site.addzero.vibepocket.screens.PlaceholderScreen
import site.addzero.vibepocket.screens.WelcomeScreenWrapper

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


    Theme(style = ModernMinimalDark) {
        // A surface container using the 'background' color from the theme
        val sidebarState = remember { SidebarState() }
        CompositionLocalProvider(LocalSidebarState provides sidebarState) {
            if (!isSetupDone) {
                WelcomeScreenWrapper(
                    backStack = backStack,
                    homeRoute = homeRoute,
                    onSetupComplete = { token, url ->
                        isSetupDone = true
                    }
                )
            } else {
                // 主界面：侧边栏 + NavDisplay
                MainScreen(menuTree, backStack)
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
                        val function = iocComposablesByTag["screen"]?.get(routeKey.key)?.invoke()
                        function ?: return@NavEntry PlaceholderScreen("❓", "未知页面")
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
fun MenuNodeItem(node: MenuNode, onLeafClick: (MenuNode) -> Unit, isSelected: Boolean) {
    if (node.children.isNotEmpty() || node.isVirtualParent) { // This is a Parent or VirtualParent
        Text(
            text = node.metadata.menuNameAlias,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.styles.sidebarForeground
        )
        node.children.forEach { child ->
            MenuNodeItem(child, onLeafClick, isSelected)
        }
    } else { // This is a Leaf
        com.shadcn.ui.components.sidebar.SidebarMenuButton(
            text = node.metadata.menuNameAlias,
            onClick = { onLeafClick(node) },
            isActive = isSelected
        )
    }
}