package site.addzero.vibepocket.music

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import eu.iamkonstantin.gadulka.GadulkaPlayer
import eu.iamkonstantin.gadulka.PlayerState
import eu.iamkonstantin.gadulka.rememberPlayerState

/**
 * Gadulka 播放器 Demo
 *
 * 一个简洁的音乐播放器 UI，使用 gadulka 库实现跨平台音频播放。
 * 支持：播放/暂停、停止、音量调节、进度条、播放速率调节。
 */
@Composable
fun GadulkaPlayerDemo(
    modifier: Modifier = Modifier,
) {
    val player = remember { GadulkaPlayer() }
    val state = rememberPlayerState(player)

    // 示例音频 URL（公共域 MP3）
    var audioUrl by remember {
        mutableStateOf("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3")
    }
    var volume by remember { mutableFloatStateOf(1.0f) }
    var playbackRate by remember { mutableFloatStateOf(1.0f) }

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "🎵 Gadulka Player Demo",
            style = MaterialTheme.typography.headlineSmall,
        )

        // URL 输入
        OutlinedTextField(
            value = audioUrl,
            onValueChange = { audioUrl = it },
            label = { Text("音频 URL") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        // 状态显示
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("状态:", style = MaterialTheme.typography.labelMedium)
                    val statusText = when (state.playerState) {
                        PlayerState.IDLE -> "空闲"
                        PlayerState.BUFFERING -> "缓冲中..."
                        PlayerState.PLAYING -> "播放中 ▶"
                        PlayerState.PAUSED -> "已暂停 ⏸"
                        PlayerState.ERROR -> "错误 ✗"
                    }
                    val statusColor = when (state.playerState) {
                        PlayerState.PLAYING -> MaterialTheme.colorScheme.primary
                        PlayerState.ERROR -> MaterialTheme.colorScheme.error
                        PlayerState.BUFFERING -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    Text(statusText, color = statusColor, style = MaterialTheme.typography.bodyMedium)
                }

                // 进度条
                if (state.duration > 0) {
                    Column {
                        LinearProgressIndicator(
                            progress = { (state.position.toFloat() / state.duration).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                formatTime(state.position),
                                style = MaterialTheme.typography.labelSmall,
                            )
                            Text(
                                formatTime(state.duration),
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                }
            }
        }

        // 播放控制按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        ) {
            FilledTonalButton(
                onClick = { player.play(audioUrl) },
                enabled = state.playerState != PlayerState.PLAYING,
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "播放")
                Spacer(Modifier.width(4.dp))
                Text("播放")
            }

            FilledTonalButton(
                onClick = { player.pause() },
                enabled = state.playerState == PlayerState.PLAYING,
            ) {
                Icon(Icons.Default.Pause, contentDescription = "暂停")
                Spacer(Modifier.width(4.dp))
                Text("暂停")
            }

            FilledTonalButton(
                onClick = { player.stop() },
                enabled = state.playerState == PlayerState.PLAYING || state.playerState == PlayerState.PAUSED,
            ) {
                Icon(Icons.Default.Stop, contentDescription = "停止")
                Spacer(Modifier.width(4.dp))
                Text("停止")
            }
        }

        // 音量控制
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.Default.VolumeDown, contentDescription = null)
            Slider(
                value = volume,
                onValueChange = {
                    volume = it
                    player.setVolume(it)
                },
                valueRange = 0f..1f,
                modifier = Modifier.weight(1f),
            )
            Icon(Icons.Default.VolumeUp, contentDescription = null)
            Text("${(volume * 100).toInt()}%", style = MaterialTheme.typography.labelSmall)
        }

        // 播放速率
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("速率:", style = MaterialTheme.typography.labelMedium)
            listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { rate ->
                FilterChip(
                    selected = playbackRate == rate,
                    onClick = {
                        playbackRate = rate
                        player.setPlaybackRate(rate)
                    },
                    label = { Text("${rate}x") },
                )
            }
        }
    }
}

/** 毫秒格式化为 mm:ss */
private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
