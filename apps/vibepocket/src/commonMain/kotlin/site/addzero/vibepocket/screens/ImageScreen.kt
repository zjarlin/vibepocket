package site.addzero.vibepocket.screens

import androidx.compose.runtime.Composable
import site.addzero.ioc.annotation.Bean

@Composable
@Bean(tags = ["screen"])
fun ImageScreen() {
    PlaceholderScreen("\uD83D\uDDBC\uFE0F", "Image tools scaffold")
}
