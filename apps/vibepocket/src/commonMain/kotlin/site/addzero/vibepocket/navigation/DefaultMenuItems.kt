package site.addzero.vibepocket.navigation

val defaultMenuItems = listOf(
    MenuMetadata(
        routeKey = "site.addzero.vibepocket.navigation.group.Studio",
        menuNameAlias = "Studio",
        icon = "\uD83E\uDDF1",
        sortOrder = 0,
    ),
    MenuMetadata(
        routeKey = "site.addzero.vibepocket.music.MusicVibeScreen",
        menuNameAlias = "Music",
        icon = "\uD83C\uDFB5",
        parentRouteKey = "site.addzero.vibepocket.navigation.group.Studio",
        sortOrder = 0,
    ),
    MenuMetadata(
        routeKey = "site.addzero.vibepocket.screens.ImageScreen",
        menuNameAlias = "Image",
        icon = "\uD83D\uDDBC\uFE0F",
        parentRouteKey = "site.addzero.vibepocket.navigation.group.Studio",
        sortOrder = 1,
    ),
    MenuMetadata(
        routeKey = "site.addzero.vibepocket.screens.VideoScreen",
        menuNameAlias = "Video",
        icon = "\uD83C\uDFA5",
        parentRouteKey = "site.addzero.vibepocket.navigation.group.Studio",
        sortOrder = 2,
    ),
    MenuMetadata(
        routeKey = "site.addzero.vibepocket.settings.SettingsPage",
        menuNameAlias = "Settings",
        icon = "\u2699\uFE0F",
        sortOrder = 3,
    ),
)

object DefaultMenuMetadataProvider : MenuMetadataProvider {
    override fun loadMenuMetadata(): List<MenuMetadata> = defaultMenuItems
}
