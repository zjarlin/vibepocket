package site.addzero.vibepocket.navigation

import androidx.compose.runtime.Composable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

typealias RouteComposable = @Composable () -> Unit

fun interface MenuMetadataProvider {
    fun loadMenuMetadata(): List<MenuMetadata>
}

class StaticMenuMetadataProvider(
    private val items: List<MenuMetadata>,
) : MenuMetadataProvider {
    override fun loadMenuMetadata(): List<MenuMetadata> = items
}

class JsonMenuMetadataProvider(
    private val payload: String,
    private val json: Json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    },
) : MenuMetadataProvider {
    override fun loadMenuMetadata(): List<MenuMetadata> {
        return json.decodeFromString(ListSerializer(MenuMetadata.serializer()), payload)
    }
}

interface RouteRendererSpi {
    fun renderers(): Map<String, RouteComposable>
}

class StaticRouteRendererSpi(
    private val routeRenderers: Map<String, RouteComposable>,
) : RouteRendererSpi {
    override fun renderers(): Map<String, RouteComposable> = routeRenderers
}

class RouteRendererRegistry private constructor(
    private val routeRenderers: Map<String, RouteComposable>,
) {
    fun resolve(routeKey: String): RouteComposable? = routeRenderers[routeKey]

    fun contains(routeKey: String): Boolean = routeKey in routeRenderers

    fun routeKeys(): Set<String> = routeRenderers.keys

    companion object {
        fun from(spis: List<RouteRendererSpi>): RouteRendererRegistry {
            val resolved = linkedMapOf<String, RouteComposable>()
            spis.forEach { spi ->
                spi.renderers().forEach { (routeKey, renderer) ->
                    require(routeKey.isNotBlank()) {
                        "routeKey must not be blank"
                    }
                    require(routeKey !in resolved) {
                        "Duplicate route renderer registered for routeKey=$routeKey"
                    }
                    resolved[routeKey] = renderer
                }
            }
            return RouteRendererRegistry(resolved)
        }
    }
}

data class MenuNavigationRuntime(
    val menuItems: List<MenuMetadata>,
    val menuTree: List<MenuNode>,
    val defaultRoute: RouteKey?,
    val routeRendererRegistry: RouteRendererRegistry,
)

object MenuNavigationRuntimeFactory {
    fun create(
        metadataProviders: List<MenuMetadataProvider>,
        rendererSpis: List<RouteRendererSpi>,
    ): MenuNavigationRuntime {
        val menuItems = collectMenuMetadata(metadataProviders)
        val menuTree = MenuTreeBuilder.buildTree(menuItems)
        val defaultRoute = MenuTreeBuilder
            .flattenVisibleLeaves(menuTree)
            .firstOrNull()
            ?.routeKey
            ?.let(::RouteKey)

        return MenuNavigationRuntime(
            menuItems = menuItems,
            menuTree = menuTree,
            defaultRoute = defaultRoute,
            routeRendererRegistry = RouteRendererRegistry.from(rendererSpis),
        )
    }

    fun collectMenuMetadata(
        metadataProviders: List<MenuMetadataProvider>,
    ): List<MenuMetadata> {
        val resolved = linkedMapOf<String, MenuMetadata>()
        metadataProviders.forEach { provider ->
            provider.loadMenuMetadata().forEach { item ->
                require(item.routeKey.isNotBlank()) {
                    "MenuMetadata.routeKey must not be blank"
                }
                require(item.routeKey !in resolved) {
                    "Duplicate menu routeKey detected: ${item.routeKey}"
                }
                resolved[item.routeKey] = item
            }
        }
        return resolved.values.toList()
    }
}
