package site.addzero.vibepocket.music

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import site.addzero.component.glass.*
import site.addzero.vibepocket.api.SunoApiClient
import site.addzero.vibepocket.model.*

@Serializable
private data class VocalRemovalConfigResp(val key: String, val value: String?)

/** 从内嵌 server 读取配置 */
private suspend fun fetchVocalRemovalConfig(key: String): String? {
    val client = HttpClient { install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) } }
    return try {
        client.get("http://localhost:8080/api/config/$key").body<VocalRemovalConfigResp>().value
    } catch (_: Exception) {
        null
    } finally {
        client.close()
    }
}

/**
 * 人声分离确认 Dialog
 *
 * 接收 audioId 和 taskId，确认后调用 SunoApiClient.vocalRemoval()，
 * 轮询任务进度并展示分离后的音轨结果。
 */
@Composable
fun VocalRemovalConfirmDialog(
    audioId: String,
    taskId: String,
    onDismiss: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    // ── 提交状态 ──
    var isSubmitting by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // ── 结果 ──
    var resultDetail by remember { mutableStateOf<SunoTaskDetail?>(null) }

    // ── 播放状态 ──
    val currentTrackId by AudioPlayerManager.currentTrackId.collectAsState()
    val playerState by AudioPlayerManager.playerState.collectAsState()
    val progress by AudioPlayerManager.progress.collectAsState()
    val position by AudioPlayerManager.position.collectAsState()
    val duration by AudioPlayerManager.duration.collectAsState()

    AlertDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        confirmButton = {},
        title = {
            Text(
                text = "🎤 人声分离",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        },
        containerColor = Color(0xFF1A1A2E),
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // ── 确认区域（未提交或提交中时显示） ──
                if (resultDetail == null) {
                    Text(
                        text = "将对该 Track 执行人声分离，生成纯伴奏和纯人声音轨。",
                        color = GlassTheme.TextTertiary,
                        fontSize = 13.sp,
                    )

                    // 提交按钮
                    NeonGlassButton(
                        text = if (isSubmitting) "⏳ 处理中..." else "🚀 开始分离",
                        onClick = {
                            if (isSubmitting) return@NeonGlassButton
                            isSubmitting = true
                            errorMessage = null
                            statusText = "正在提交..."

                            scope.launch {
                                try {
                                    val token = fetchVocalRemovalConfig("suno_api_token") ?: ""
                                    val url = fetchVocalRemovalConfig("suno_api_base_url")
                                        ?.ifBlank { null }
                                        ?: "https://api.sunoapi.org/api/v1"
                                    val client = SunoApiClient(apiToken = token, baseUrl = url)

                                    val request = SunoVocalRemovalRequest(
                                        taskId = taskId,
                                        audioId = audioId,
                                    )

                                    val newTaskId = client.vocalRemoval(request)
                                    statusText = "已提交，轮询中..."

                                    // 轮询等待完成
                                    val detail = client.waitForCompletion(
                                        taskId = newTaskId,
                                        maxWaitMs = 600_000L,
                                        pollIntervalMs = 30_000L,
                                        onStatusUpdate = { detail ->
                                            statusText = detail?.displayStatus ?: "轮询中..."
                                        },
                                    )
                                    resultDetail = detail
                                    statusText = null
                                } catch (e: Exception) {
                                    errorMessage = "❌ ${e.message}"
                                    statusText = null
                                } finally {
                                    isSubmitting = false
                                }
                            }
                        },
                        glowColor = GlassColors.NeonCyan,
                        enabled = !isSubmitting,
                    )

                    // 状态文本
                    statusText?.let { status ->
                        Text(text = status, color = GlassColors.NeonCyan, fontSize = 12.sp)
                    }

                    // 错误信息
                    errorMessage?.let { error ->
                        Text(text = error, color = GlassColors.NeonMagenta, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        GlassButton(
                            text = "🔄 重试",
                            onClick = { errorMessage = null },
                        )
                    }
                }

                // ── 结果展示区域 ──
                resultDetail?.let { detail ->
                    Text(
                        text = "✅ 人声分离完成",
                        color = GlassColors.NeonCyan,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                    )

                    val tracks = detail.response?.sunoData ?: emptyList()
                    if (tracks.isNotEmpty()) {
                        tracks.forEach { track ->
                            val trackId = track.id
                            val trackPlayerState = if (trackId != null && currentTrackId == trackId) {
                                TrackPlayerState(
                                    isPlaying = playerState == PlayerState.PLAYING,
                                    progress = progress,
                                    currentTime = AudioPlayerManager.formatTime(position),
                                    totalTime = AudioPlayerManager.formatTime(duration),
                                )
                            } else {
                                TrackPlayerState()
                            }

                            TrackCard(
                                track = track,
                                taskId = detail.taskId ?: taskId,
                                isFavorite = false,
                                onFavoriteToggle = {},
                                onAction = {},
                                playerState = trackPlayerState,
                                onPlayToggle = {
                                    if (trackId == null || track.audioUrl == null) return@TrackCard
                                    when {
                                        currentTrackId == trackId && playerState == PlayerState.PLAYING ->
                                            AudioPlayerManager.pause()
                                        currentTrackId == trackId && playerState == PlayerState.PAUSED ->
                                            AudioPlayerManager.resume()
                                        else ->
                                            AudioPlayerManager.play(trackId, track.audioUrl!!)
                                    }
                                },
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    GlassButton(
                        text = "关闭",
                        onClick = onDismiss,
                    )
                }
            }
        },
    )
}
