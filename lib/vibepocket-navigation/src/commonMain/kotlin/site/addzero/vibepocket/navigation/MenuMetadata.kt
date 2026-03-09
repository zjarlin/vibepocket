package site.addzero.vibepocket.navigation

import kotlinx.serialization.Serializable

@Serializable
data class MenuMetadata(
    /**
     * Unique route key for the sidebar node.
     *
     * Leaf nodes should use the fully qualified name of the target composable.
     * Group nodes can use any stable unique key.
     */
    val routeKey: String,
    val menuNameAlias: String,
    /**
     * Plain text icon token so metadata stays JSON-serializable and does not
     * depend on ImageVector.
     */
    val icon: String? = null,
    val parentRouteKey: String? = null,
    val visible: Boolean = true,
    val sortOrder: Int = 0,
)

data class MenuNode(
    val metadata: MenuMetadata,
    val children: List<MenuNode> = emptyList(),
    val isVirtualParent: Boolean = false,
)
