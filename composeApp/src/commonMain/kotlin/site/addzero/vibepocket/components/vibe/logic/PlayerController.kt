package com.zjarlin.vibe.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationSeconds: Int,
    val coverUrl: String? = null
)

class MusicPlayerController {
    // Observable state
    private val _isPlaying = mutableStateOf(false)
    val isPlaying: Boolean get() = _isPlaying.value

    private val _currentProgress = mutableStateOf(0f)
    val currentProgress: Float get() = _currentProgress.value
    
    // Current Track
    private val _currentTrack = mutableStateOf(
        Track("1", "Midnight City", "M83", "Hurry Up, We're Dreaming", 243)
    )
    val currentTrack: Track get() = _currentTrack.value

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
    }

    fun seekTo(progress: Float) {
        _currentProgress.value = progress.coerceIn(0f, 1f)
    }

    fun nextTrack() {
        // Logic to switch to next track
        _currentTrack.value = _currentTrack.value.copy(title = "Next Track Demo", id = "2")
        _currentProgress.value = 0f
        _isPlaying.value = true
    }

    fun previousTrack() {
        // Logic to switch to prev track
        _currentTrack.value = _currentTrack.value.copy(title = "Prev Track Demo", id = "0")
        _currentProgress.value = 0f
        _isPlaying.value = true
    }
}
