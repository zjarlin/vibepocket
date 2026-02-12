package site.addzero.vibepocket.music

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import eu.iamkonstantin.kotlin.gadulka.GadulkaPlayer
import eu.iamkonstantin.kotlin.gadulka.GadulkaPlayerState
import eu.iamkonstantin.kotlin.gadulka.rememberGadulkaLiveState
import site.addzero.ioc.annotation.Bean

/**
 * Gadulka 播放器 Demo
 *
 * 一个简洁的音乐播放器 UI，使用 gadulka 库实现跨平台音频播放。
 * 支持：播放/暂停、停止、音量调节、进度条、播放速率调节。
 */
@Composable
@Bean(tags = ["screen"])
fun GadulkaPlayerDemo(
    modifier: Modifier = Modifier,
) {
    val player = remember { GadulkaPlayer() }
    val liveState = rememberGadulkaLiveState()

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
                    val statusText = when (liveState.state) {
                        GadulkaPlayerState.IDLE -> "空闲"
                        GadulkaPlayerState.BUFFERING -> "缓冲中..."
                        GadulkaPlayerState.PLAYING -> "播放中 ▶"
                        GadulkaPlayerState.PAUSED -> "已暂停 ⏸"
                    }
                    val statusColor = when (liveState.state) {
                        GadulkaPlayerState.PLAYING -> MaterialTheme.colorScheme.primary
                        GadulkaPlayerState.BUFFERING -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    Text(statusText, color = statusColor, style = MaterialTheme.typography.bodyMedium)
                }

                // 进度条
                if (liveState.duration > 0) {
                    Column {
                        LinearProgressIndicator(
                            progress = { (liveState.position.toFloat() / liveState.duration).coerceIn(0f, 1f) },
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
                                formatTime(liveState.position),
                                style = MaterialTheme.typography.labelSmall,
                            )
                            Text(
                                formatTime(liveState.duration),
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
                enabled = liveState.state != GadulkaPlayerState.PLAYING,
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "播放")
                Spacer(Modifier.width(4.dp))
                Text("播放")
            }

            FilledTonalButton(
                onClick = { player.pause() },
                enabled = liveState.state == GadulkaPlayerState.PLAYING,
            ) {
                Icon(Icons.Default.Pause, contentDescription = "暂停")
                Spacer(Modifier.width(4.dp))
                Text("暂停")
            }

            FilledTonalButton(
                onClick = { player.stop() },
                enabled = liveState.state == GadulkaPlayerState.PLAYING || liveState.state == GadulkaPlayerState.PAUSED,
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
                        player.setRate(rate)
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
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}
