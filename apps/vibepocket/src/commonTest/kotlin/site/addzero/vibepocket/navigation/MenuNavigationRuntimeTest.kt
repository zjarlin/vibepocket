package site.addzero.vibepocket.navigation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class MenuNavigationRuntimeTest {

    private val musicRenderer: RouteComposable = { }
    private val settingsRenderer: RouteComposable = { }

    @Test
    fun routeRendererRegistry_mergesRoutesFromMultipleSpis() {
        val registry = RouteRendererRegistry.from(
            listOf(
                StaticRouteRendererSpi(
                    mapOf("site.addzero.vibepocket.music.MusicVibeScreen" to musicRenderer),
                ),
                StaticRouteRendererSpi(
                    mapOf("site.addzero.vibepocket.settings.SettingsPage" to settingsRenderer),
                ),
            ),
        )

        assertSame(musicRenderer, registry.resolve("site.addzero.vibepocket.music.MusicVibeScreen"))
        assertSame(settingsRenderer, registry.resolve("site.addzero.vibepocket.settings.SettingsPage"))
        assertEquals(
            setOf(
                "site.addzero.vibepocket.music.MusicVibeScreen",
                "site.addzero.vibepocket.settings.SettingsPage",
            ),
            registry.routeKeys(),
        )
    }

    @Test
    fun routeRendererRegistry_duplicateRouteKeyThrows() {
        assertFailsWith<IllegalArgumentException> {
            RouteRendererRegistry.from(
                listOf(
                    StaticRouteRendererSpi(mapOf("dup.route" to musicRenderer)),
                    StaticRouteRendererSpi(mapOf("dup.route" to settingsRenderer)),
                ),
            )
        }
    }

    @Test
    fun jsonMenuMetadataProvider_decodesSerializableMetadata() {
        val provider = JsonMenuMetadataProvider(
            """
            [
              {
                "routeKey": "site.addzero.vibepocket.music.MusicVibeScreen",
                "menuNameAlias": "Music",
                "icon": "music",
                "sortOrder": 10
              }
            ]
            """.trimIndent(),
        )

        val metadata = provider.loadMenuMetadata()

        assertEquals(1, metadata.size)
        assertEquals("site.addzero.vibepocket.music.MusicVibeScreen", metadata.single().routeKey)
        assertEquals("Music", metadata.single().menuNameAlias)
    }

    @Test
    fun create_usesFirstVisibleLeafAsDefaultRoute() {
        val runtime = MenuNavigationRuntimeFactory.create(
            metadataProviders = listOf(
                StaticMenuMetadataProvider(
                    listOf(
                        MenuMetadata(
                            routeKey = "site.addzero.vibepocket.navigation.group.Root",
                            menuNameAlias = "Root",
                            sortOrder = 0,
                        ),
                        MenuMetadata(
                            routeKey = "hidden.route",
                            menuNameAlias = "Hidden",
                            parentRouteKey = "site.addzero.vibepocket.navigation.group.Root",
                            visible = false,
                            sortOrder = 0,
                        ),
                        MenuMetadata(
                            routeKey = "visible.route",
                            menuNameAlias = "Visible",
                            parentRouteKey = "site.addzero.vibepocket.navigation.group.Root",
                            sortOrder = 1,
                        ),
                    ),
                ),
            ),
            rendererSpis = emptyList(),
        )

        assertEquals("visible.route", runtime.defaultRoute?.key)
    }

    @Test
    fun collectMenuMetadata_duplicateRouteKeyThrows() {
        assertFailsWith<IllegalArgumentException> {
            MenuNavigationRuntimeFactory.collectMenuMetadata(
                listOf(
                    StaticMenuMetadataProvider(
                        listOf(
                            MenuMetadata(routeKey = "dup.route", menuNameAlias = "A"),
                        ),
                    ),
                    StaticMenuMetadataProvider(
                        listOf(
                            MenuMetadata(routeKey = "dup.route", menuNameAlias = "B"),
                        ),
                    ),
                ),
            )
        }
    }
}
