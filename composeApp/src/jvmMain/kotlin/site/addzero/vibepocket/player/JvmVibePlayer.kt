package site.addzero.vibepocket.player

import javazoom.jl.player.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.net.URL

class JvmVibePlayer : VibePlayer {
    private var player: Player? = null
    private var playerJob: Job? = null
    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    override fun play(url: String) {
        stop()
        _isPlaying.value = true
        playerJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                val inputStream = BufferedInputStream(URL(url).openStream())
                player = Player(inputStream)
                player?.play()
                _isPlaying.value = false
            } catch (e: Exception) {
                e.printStackTrace()
                _isPlaying.value = false
            }
        }
    }

    override fun pause() {
        // JLayer Basic Player doesn't support pause/resume well without complex logic.
        // For this simple test, we'll just stop. User asked to "test playback".
        stop()
    }

    override fun resume() {
        // Not implemented for simple JLayer test
    }

    override fun stop() {
        try {
            player?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        playerJob?.cancel()
        _isPlaying.value = false
    }
}
