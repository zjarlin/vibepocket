package site.addzero.vibepocket

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

import site.addzero.vibepocket.data.JvmMusicRepository
import site.addzero.vibepocket.player.JvmVibePlayer

fun main() = application {
    val musicRepository = site.addzero.vibepocket.data.JvmMusicRepository()
    val player = site.addzero.vibepocket.player.JvmVibePlayer()
    Window(
        onCloseRequest = ::exitApplication,
        title = "vibepocket",
    ) {
        App(musicRepository, player)
    }
}