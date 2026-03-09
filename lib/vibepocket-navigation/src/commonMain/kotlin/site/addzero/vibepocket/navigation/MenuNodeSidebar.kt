package site.addzero.vibepocket.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MenuNodeSidebar(
    menuTree: List<MenuNode>,
    selectedRouteKey: String,
    onLeafClick: (MenuNode) -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    width: Dp = 260.dp,
    indentPerLevel: Dp = 16.dp,
    initialExpandedKeys: Set<String>? = null,
) {
    val chromeColors = NavigationChromeTheme.colors
    val expandedState = remember {
        mutableStateMapOf<String, Boolean>().apply {
            initialExpandedKeys?.forEach { put(it, true) }
        }
    }
    val defaultExpanded = initialExpandedKeys == null
    val shape = RoundedCornerShape(28.dp)

    Column(
        modifier = modifier
            .width(width)
            .fillMaxHeight()
            .padding(12.dp)
            .clip(shape)
            .background(chromeColors.sidebarSurface)
            .border(1.dp, chromeColors.sidebarBorder, shape)
            .padding(vertical = 18.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        if (title != null) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        menuTree.forEach { node ->
            if (node.metadata.visible) {
                TreeNodeItem(
                    node = node,
                    depth = 0,
                    selectedRouteKey = selectedRouteKey,
                    onLeafClick = onLeafClick,
                    expandedState = expandedState,
                    defaultExpanded = defaultExpanded,
                    indentPerLevel = indentPerLevel,
                )
            }
        }
    }
}

@Composable
private fun TreeNodeItem(
    node: MenuNode,
    depth: Int,
    selectedRouteKey: String,
    onLeafClick: (MenuNode) -> Unit,
    expandedState: MutableMap<String, Boolean>,
    defaultExpanded: Boolean,
    indentPerLevel: Dp,
) {
    val chromeColors = NavigationChromeTheme.colors
    val visibleChildren = node.children.filter { it.metadata.visible }
    val hasChildren = visibleChildren.isNotEmpty()
    val isExpanded = expandedState[node.metadata.routeKey] ?: defaultExpanded
    val isSelected = node.metadata.routeKey == selectedRouteKey
    val itemShape = RoundedCornerShape(18.dp)
    val contentColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        hasChildren -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 10.dp + indentPerLevel * depth,
                end = 10.dp,
                top = 2.dp,
                bottom = 2.dp,
            )
            .clip(itemShape)
            .background(if (isSelected) chromeColors.selectedItemContainer else Color.Transparent)
            .clickable {
                if (hasChildren) {
                    expandedState[node.metadata.routeKey] = !isExpanded
                } else {
                    onLeafClick(node)
                }
            }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        if (hasChildren) {
            Text(
                text = if (isExpanded) "-" else "+",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
            )
            Spacer(modifier = Modifier.width(6.dp))
        }

        node.metadata.icon?.let { icon ->
            Text(
                text = icon,
                fontSize = 15.sp,
            )
            Spacer(modifier = Modifier.width(10.dp))
        }

        Text(
            text = node.metadata.menuNameAlias,
            color = contentColor,
            fontSize = 14.sp,
            fontWeight = when {
                isSelected -> FontWeight.SemiBold
                hasChildren -> FontWeight.Medium
                else -> FontWeight.Normal
            },
        )
    }

    AnimatedVisibility(
        visible = hasChildren && isExpanded,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        Column {
            visibleChildren.forEach { child ->
                TreeNodeItem(
                    node = child,
                    depth = depth + 1,
                    selectedRouteKey = selectedRouteKey,
                    onLeafClick = onLeafClick,
                    expandedState = expandedState,
                    defaultExpanded = defaultExpanded,
                    indentPerLevel = indentPerLevel,
                )
            }
        }
    }
}
