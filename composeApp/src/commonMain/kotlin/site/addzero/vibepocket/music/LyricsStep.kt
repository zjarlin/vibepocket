package site.addzero.vibepocket.music

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import site.addzero.component.glass.*
import site.addzero.vibepocket.model.NeteaseSearchSong
import site.addzero.vibepocket.service.MusicSearchService

/**
 * 第一步：确认歌词
 */
@Composable
fun LyricsStep(
    lyrics: String,
    onLyricsChange: (String) -> Unit,
    songName: String,
    onSongNameChange: (String) -> Unit,
    artistName: String,
    onArtistNameChange: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var isSearching by remember { mutableStateOf(false) }
    var searchError by remember { mutableStateOf<String?>(null) }
    // 搜索结果列表
    var searchResults by remember { mutableStateOf<List<NeteaseSearchSong>>(emptyList()) }
    // 正在加载歌词的歌曲 ID
    var loadingLyricId by remember { mutableStateOf<Long?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // ── 搜索区域 ──
        NeonGlassCard(
            modifier = Modifier.fillMaxWidth(),
            glowColor = GlassColors.NeonCyan
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🔍 搜索歌词（可选）",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GlassTextField(
                        value = songName,
                        onValueChange = onSongNameChange,
                        placeholder = "歌名",
                        modifier = Modifier.weight(1f)
                    )
                    GlassTextField(
                        value = artistName,
                        onValueChange = onArtistNameChange,
                        placeholder = "歌手（可选）",
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                GlassButton(
                    text = if (isSearching) "⏳ 搜索中..." else "搜索歌曲",
                    onClick = {
                        if (isSearching) return@GlassButton
                        isSearching = true
                        searchError = null
                        searchResults = emptyList()
                        scope.launch {
                            try {
                                val results = MusicSearchService.searchSongs(
                                    songName = songName,
                                    artistName = artistName.ifBlank { null },
                                )
                                searchResults = results
                                if (results.isEmpty()) {
                                    searchError = "未找到相关歌曲"
                                }
                            } catch (e: Exception) {
                                searchError = "搜索失败: ${e.message}"
                            } finally {
                                isSearching = false
                            }
                        }
                    },
                    enabled = songName.isNotBlank() && !isSearching
                )

                searchError?.let { error ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = error, color = GlassColors.NeonMagenta, fontSize = 12.sp)
                }
            }
        }

        // ── 搜索结果列表（带封面图） ──
        if (searchResults.isNotEmpty()) {
            NeonGlassCard(
                modifier = Modifier.fillMaxWidth(),
                glowColor = GlassColors.NeonPurple
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🎵 搜索结果（${searchResults.size} 首）· 点击获取歌词",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 320.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(searchResults, key = { it.id }) { song ->
                            SongResultItem(
                                song = song,
                                isLoading = loadingLyricId == song.id,
                                onClick = {
                                    if (loadingLyricId != null) return@SongResultItem
                                    loadingLyricId = song.id
                                    scope.launch {
                                        try {
                                            val lyricText = MusicSearchService.getLyric(song.id)
                                            if (lyricText != null) {
                                                onLyricsChange(lyricText)
                                                onSongNameChange(song.name)
                                                val artist = song.artistNames
                                                if (artist.isNotBlank()) onArtistNameChange(artist)
                                            } else {
                                                searchError = "「${song.name}」暂无歌词"
                                            }
                                        } catch (e: Exception) {
                                            searchError = "获取歌词失败: ${e.message}"
                                        } finally {
                                            loadingLyricId = null
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // ── 歌词编辑区域 ──
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📝 歌词内容",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "直接粘贴歌词，或通过上方搜索自动填入",
                    color = GlassTheme.TextTertiary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                GlassTextArea(
                    value = lyrics,
                    onValueChange = onLyricsChange,
                    placeholder = "在此输入或粘贴歌词...\n\n支持带时间轴的 LRC 格式，例如：\n[00:33.71]阿刁\n[00:36.31]住在西藏的某个地方",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp, max = 400.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "已输入 ${lyrics.lines().count { it.isNotBlank() }} 行",
                    color = GlassTheme.TextTertiary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

// ── 单条搜索结果 ──────────────────────────────────────────

@Composable
private fun SongResultItem(
    song: NeteaseSearchSong,
    isLoading: Boolean,
    onClick: () -> Unit,
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isLoading, onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 封面图
            val coverUrl = song.coverUrl
            if (coverUrl != null) {
                AsyncImage(
                    model = coverUrl,
                    contentDescription = song.name,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop,
                )
            } else {
                // 无封面占位
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🎵", fontSize = 20.sp)
                }
            }

            // 歌曲信息
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.name,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = buildString {
                        append(song.artistNames)
                        song.album?.name?.let { if (it.isNotBlank()) append(" · $it") }
                    },
                    color = GlassTheme.TextTertiary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 状态提示
            if (isLoading) {
                Text("⏳", fontSize = 16.sp)
            }
        }
    }
}
