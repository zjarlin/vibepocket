package site.addzero.vibepocket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

import site.addzero.vibepocket.data.AndroidMusicRepository
import site.addzero.vibepocket.player.AndroidVibePlayer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val musicRepository = AndroidMusicRepository()
        val player = AndroidVibePlayer()
        setContent {
            App(musicRepository, player)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}