package site.addzero.vibepocket.screens

import androidx.compose.runtime.Composable
import site.addzero.ioc.annotation.Bean

@Composable
@Bean(tags = ["screen"])
fun VideoScreen() {
    PlaceholderScreen("\uD83C\uDFA5", "Video tools scaffold")
}
