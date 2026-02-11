package site.addzero.vibepocket.plugins

import io.ktor.server.application.*
import site.addzero.ioc.annotation.Bean
import site.addzero.ktor.banner.Banner

@Bean
fun Application.configureBanner() {
    install(Banner) {
        text = "VIBEPOCKET"
        subtitle = "Music Vibe Generator"
    }
}
