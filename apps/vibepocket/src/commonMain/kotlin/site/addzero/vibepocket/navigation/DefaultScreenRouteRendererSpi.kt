package site.addzero.vibepocket.navigation

import site.addzero.vibepocket.music.ioc.generated.iocComposablesByTag

object DefaultScreenRouteRendererSpi : RouteRendererSpi {
    override fun renderers(): Map<String, RouteComposable> {
        return iocComposablesByTag["screen"] ?: emptyMap()
    }
}
