package site.addzero.vibepocket

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun main() {
    // 内嵌 Ktor server，桌面端自带后端
    val server = startEmbeddedServer()
    GlobalScope.launch(Dispatchers.IO) {
        server.start(wait = false)
    }

    application {
        Window(
            onCloseRequest = {
                server.stop(1000, 2000)
                exitApplication()
            },
            title = "Vibepocket",
        ) {
            App()
        }
    }
}
