package site.addzero.vibepocket.player

import kotlinx.coroutines.flow.StateFlow

interface VibePlayer {
    fun play(url: String)
    fun pause()
    fun resume()
    fun stop()
    val isPlaying: StateFlow<Boolean>
}
