package site.addzero.vibepocket.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BorderStroke
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.shadcn.ui.components.sidebar.SidebarLayout
import com.shadcn.ui.components.sidebar.SidebarProvider
import com.shadcn.ui.components.sidebar.SidebarTrigger

@Composable
fun MenuNavigationScaffold(
    appTitle: String,
    menuTree: List<MenuNode>,
    backStack: SnapshotStateList<RouteKey>,
    routeRendererRegistry: RouteRendererRegistry,
    themeMode: NavigationThemeMode,
    onThemeModeChange: (NavigationThemeMode) -> Unit,
    modifier: Modifier = Modifier,
    missingRouteContent: @Composable (routeKey: String) -> Unit = { routeKey ->
        MissingRouteFallback(routeKey = routeKey)
    },
) {
    val chromeColors = NavigationChromeTheme.colors
    val topBarShape = RoundedCornerShape(24.dp)
    val contentShape = RoundedCornerShape(28.dp)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        chromeColors.windowGradientStart,
                        chromeColors.windowGradientEnd,
                    ),
                ),
            ),
            .padding(8.dp),
    ) {
        SidebarProvider {
            SidebarLayout(
                modifier = Modifier.fillMaxSize(),
                sidebarContent = {
                    val sidebarState = com.shadcn.ui.components.sidebar.LocalSidebarState.current
                    MenuNodeSidebar(
                        menuTree = menuTree,
                        selectedRouteKey = backStack.lastOrNull()?.key.orEmpty(),
                        onLeafClick = { leaf ->
                            backStack.clear()
                            backStack.add(RouteKey(leaf.metadata.routeKey))
                            if (sidebarState.isMobile) {
                                sidebarState.closeSidebar()
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                        title = appTitle,
                    )
                },
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .clip(topBarShape)
                            .background(chromeColors.headerSurface)
                            .border(1.dp, chromeColors.headerBorder, topBarShape)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SidebarTrigger()
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = appTitle,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        ThemeModeButton(
                            themeMode = themeMode,
                            onThemeModeChange = onThemeModeChange,
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        shape = contentShape,
                        color = chromeColors.contentSurface,
                        border = BorderStroke(1.dp, chromeColors.contentBorder),
                    ) {
                        NavDisplay(
                            backStack = backStack,
                            onBack = {
                                if (backStack.size > 1) {
                                    backStack.removeAt(backStack.lastIndex)
                                }
                            },
                            entryProvider = { routeKey ->
                                NavEntry(routeKey) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(18.dp),
                                    ) {
                                        val renderer = routeRendererRegistry.resolve(routeKey.key)
                                        if (renderer == null) {
                                            missingRouteContent(routeKey.key)
                                        } else {
                                            renderer()
                                        }
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeModeButton(
    themeMode: NavigationThemeMode,
    onThemeModeChange: (NavigationThemeMode) -> Unit,
) {
    FilledTonalButton(
        onClick = { onThemeModeChange(themeMode.toggled()) },
    ) {
        val nextModeLabel = if (themeMode == NavigationThemeMode.Dark) "Light" else "Dark"
        Text(text = nextModeLabel)
    }
}

@Composable
private fun MissingRouteFallback(routeKey: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Unregistered route",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = routeKey,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}
