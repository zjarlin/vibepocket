package site.addzero.vibepocket.music

import eu.iamkonstantin.kotlin.gadulka.GadulkaPlayer
import eu.iamkonstantin.kotlin.gadulka.GadulkaPlayerState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 播放器状态枚举，对 GadulkaPlayerState 的简化映射
 */
enum class PlayerState {
    IDLE, BUFFERING, PLAYING, PAUSED, ERROR
}

/**
 * 全局单例播放管理器，基于 GadulkaPlayer。
 *
 * 确保同时只有一首 Track 在播放（需求 1.4），
 * 通过 StateFlow 暴露播放状态供 UI 层观察。
 */
object AudioPlayerManager {

    private val player = GadulkaPlayer()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var pollingJob: Job? = null

    // ── 内部可变状态 ──────────────────────────────────────────
    private val _currentTrackId = MutableStateFlow<String?>(null)
    private val _playerState = MutableStateFlow(PlayerState.IDLE)
    private val _progress = MutableStateFlow(0f)
    private val _position = MutableStateFlow(0L)
    private val _duration = MutableStateFlow(0L)

    // ── 对外只读 StateFlow ───────────────────────────────────
    val currentTrackId: StateFlow<String?> = _currentTrackId.asStateFlow()
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()
    val progress: StateFlow<Float> = _progress.asStateFlow()
    val position: StateFlow<Long> = _position.asStateFlow()
    val duration: StateFlow<Long> = _duration.asStateFlow()

    // ── 播放控制 ─────────────────────────────────────────────

    /**
     * 播放指定 Track。如果当前有其他 Track 正在播放，先停止它（需求 1.4）。
     */
    fun play(trackId: String, audioUrl: String) {
        // 停止当前播放（如果有）
        if (_currentTrackId.value != null) {
            player.stop()
        }
        _currentTrackId.value = trackId
        _playerState.value = PlayerState.BUFFERING
        _progress.value = 0f
        _position.value = 0L
        _duration.value = 0L

        player.play(audioUrl)
        startPolling()
    }

    /**
     * 暂停当前播放（需求 1.3）。
     */
    fun pause() {
        if (_playerState.value == PlayerState.PLAYING || _playerState.value == PlayerState.BUFFERING) {
            player.pause()
            _playerState.value = PlayerState.PAUSED
        }
    }

    /**
     * 恢复播放（需求 1.2 / 1.3 状态切换）。
     */
    fun resume() {
        if (_playerState.value == PlayerState.PAUSED) {
            player.play() // play() 无参数 = 从当前位置恢复
            _playerState.value = PlayerState.PLAYING
            startPolling()
        }
    }

    /**
     * 停止播放并重置状态。
     */
    fun stop() {
        pollingJob?.cancel()
        pollingJob = null
        player.stop()
        _playerState.value = PlayerState.IDLE
        _currentTrackId.value = null
        _progress.value = 0f
        _position.value = 0L
        _duration.value = 0L
    }

    /**
     * 释放播放器资源，通常在 App 退出时调用。
     */
    fun release() {
        pollingJob?.cancel()
        pollingJob = null
        player.release()
        _playerState.value = PlayerState.IDLE
        _currentTrackId.value = null
        _progress.value = 0f
        _position.value = 0L
        _duration.value = 0L
    }

    // ── 内部轮询 ─────────────────────────────────────────────

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = scope.launch {
            while (isActive) {
                syncStateFromPlayer()
                // 如果播放结束（回到 IDLE 且不是手动停止），清理状态
                if (_playerState.value == PlayerState.IDLE && _currentTrackId.value != null) {
                    _currentTrackId.value = null
                    _progress.value = 0f
                    _position.value = 0L
                    break
                }
                delay(250L) // 每 250ms 轮询一次，平衡精度与性能
            }
        }
    }

    @OptIn(eu.iamkonstantin.kotlin.gadulka.UnstableApi::class)
    private fun syncStateFromPlayer() {
        // 映射 GadulkaPlayerState → PlayerState
        val gadulkaState = player.currentPlayerState()
        _playerState.value = when (gadulkaState) {
            GadulkaPlayerState.PLAYING -> PlayerState.PLAYING
            GadulkaPlayerState.PAUSED -> PlayerState.PAUSED
            GadulkaPlayerState.BUFFERING -> PlayerState.BUFFERING
            GadulkaPlayerState.IDLE -> PlayerState.IDLE
            GadulkaPlayerState.ERROR -> PlayerState.ERROR
            null -> _playerState.value // 保持当前状态
        }

        val pos = player.currentPosition() ?: 0L
        val dur = player.currentDuration() ?: 0L
        _position.value = pos
        _duration.value = dur
        _progress.value = if (dur > 0) (pos.toFloat() / dur).coerceIn(0f, 1f) else 0f
    }

    // ── 工具方法 ─────────────────────────────────────────────

    /**
     * 格式化毫秒为 "m:ss" 字符串，供 TrackPlayerState 使用。
     */
    fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%d:%02d".format(minutes, seconds)
    }
}
