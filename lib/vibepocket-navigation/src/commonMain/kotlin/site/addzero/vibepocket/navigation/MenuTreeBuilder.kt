package site.addzero.vibepocket.navigation

object MenuTreeBuilder {

    fun buildTree(items: List<MenuMetadata>): List<MenuNode> {
        if (items.isEmpty()) return emptyList()

        detectCycles(items)

        val childrenByParent = items.groupBy { it.parentRouteKey }
        val referencedParentKeys = items.mapNotNull { it.parentRouteKey }.toSet()
        val existingKeys = items.map { it.routeKey }.toSet()
        val virtualParentKeys = referencedParentKeys - existingKeys

        fun buildNode(metadata: MenuMetadata, isVirtual: Boolean): MenuNode {
            val childNodes = (childrenByParent[metadata.routeKey] ?: emptyList())
                .sortedBy { it.sortOrder }
                .map { buildNode(it, isVirtual = false) }
            return MenuNode(
                metadata = metadata,
                children = childNodes,
                isVirtualParent = isVirtual,
            )
        }

        val virtualParentNodes = virtualParentKeys.map { parentKey ->
            val virtualMetadata = MenuMetadata(
                routeKey = parentKey,
                menuNameAlias = parentKey.substringAfterLast('.'),
                visible = true,
                sortOrder = 0,
            )
            val childNodes = (childrenByParent[parentKey] ?: emptyList())
                .sortedBy { it.sortOrder }
                .map { buildNode(it, isVirtual = false) }
            MenuNode(
                metadata = virtualMetadata,
                children = childNodes,
                isVirtualParent = true,
            )
        }

        val topLevelNodes = (childrenByParent[null] ?: emptyList())
            .sortedBy { it.sortOrder }
            .map { buildNode(it, isVirtual = false) }

        return (virtualParentNodes + topLevelNodes).sortedBy { it.metadata.sortOrder }
    }

    fun flattenVisibleLeaves(roots: List<MenuNode>): List<MenuMetadata> {
        val result = mutableListOf<MenuMetadata>()

        fun traverse(node: MenuNode) {
            if (!node.metadata.visible) return
            if (node.children.isEmpty()) {
                result += node.metadata
                return
            }
            node.children.forEach(::traverse)
        }

        roots.forEach(::traverse)
        return result
    }

    private fun detectCycles(items: List<MenuMetadata>) {
        val itemsByRouteKey = items.associateBy { it.routeKey }
        val confirmed = mutableSetOf<String>()

        for (item in items) {
            if (item.routeKey in confirmed) continue

            val visited = mutableSetOf<String>()
            val path = mutableListOf<String>()
            var current: MenuMetadata? = item

            while (current != null) {
                if (current.routeKey in confirmed) break
                if (!visited.add(current.routeKey)) {
                    val cycleStart = path.indexOf(current.routeKey)
                    val cyclePath = path.subList(cycleStart, path.size) + current.routeKey
                    throw IllegalArgumentException(
                        "Circular menu parent reference detected: ${cyclePath.joinToString(" -> ")}",
                    )
                }
                path += current.routeKey
                current = current.parentRouteKey?.let(itemsByRouteKey::get)
            }

            confirmed += visited
        }
    }
}
