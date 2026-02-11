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
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.date.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import site.addzero.component.glass.*
import site.addzero.vibepocket.api.SunoApiClient
import site.addzero.vibepocket.model.NeteaseSearchSong
import site.addzero.vibepocket.model.SunoLyricItem
import site.addzero.vibepocket.model.SunoLyricsRequest
import site.addzero.vibepocket.service.MusicSearchService

@Serializable
private data class LyricsConfigResp(val key: String, val value: String?)

/** 从内嵌 server 读取配置 */
private suspend fun fetchLyricsConfig(key: String): String? {
    val client = HttpClient { install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) } }
    return try {
        client.get("http://localhost:8080/api/config/$key").body<LyricsConfigResp>().value
    } catch (_: Exception) {
        null
    } finally {
        client.close()
    }
}

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

    // AI 生成模式开关
    var isAiMode by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // ── 模式切换按钮 ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NeonGlassButton(
                text = if (isAiMode) "📝 手动编辑" else "🤖 AI 生成歌词",
                onClick = { isAiMode = !isAiMode },
                glowColor = if (isAiMode) GlassColors.NeonCyan else GlassColors.NeonPurple,
            )
        }

        // ── AI 歌词生成区域 ──
        if (isAiMode) {
            AiLyricsGenerator(onLyricsGenerated = { generatedLyrics ->
                onLyricsChange(generatedLyrics)
                // 填入歌词后自动切回编辑模式
                isAiMode = false
            })
        }

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

// ── AI 歌词生成器 ──────────────────────────────────────────

/**
 * AI 歌词生成组件
 *
 * 输入描述提示词 → 调用 SunoApiClient.generateLyrics() → 轮询 getLyricsDetail()
 * → 多条候选以列表展示供选择 → 选中后回调 onLyricsGenerated
 */
@Composable
fun AiLyricsGenerator(
    onLyricsGenerated: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var prompt by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var candidates by remember { mutableStateOf<List<SunoLyricItem>>(emptyList()) }
    var statusText by remember { mutableStateOf<String?>(null) }

    NeonGlassCard(
        modifier = Modifier.fillMaxWidth(),
        glowColor = GlassColors.NeonPurple
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "🤖 AI 歌词生成",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "输入描述，让 AI 为你生成歌词",
                color = GlassTheme.TextTertiary,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            GlassTextField(
                value = prompt,
                onValueChange = { prompt = it },
                placeholder = "描述你想要的歌词风格和主题，例如：一首关于夏天海边的浪漫情歌",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            NeonGlassButton(
                text = if (isGenerating) "⏳ 生成中..." else "✨ 生成歌词",
                onClick = {
                    if (isGenerating) return@NeonGlassButton
                    isGenerating = true
                    errorMessage = null
                    candidates = emptyList()
                    statusText = "正在提交..."

                    scope.launch {
                        try {
                            val token = fetchLyricsConfig("suno_api_token") ?: ""
                            val url = fetchLyricsConfig("suno_api_base_url")
                                ?.ifBlank { null }
                                ?: "https://api.sunoapi.org/api/v1"
                            val client = SunoApiClient(apiToken = token, baseUrl = url)

                            // 提交歌词生成任务
                            val taskId = client.generateLyrics(SunoLyricsRequest(prompt = prompt))
                            statusText = "已提交，轮询中..."

                            // 轮询等待完成
                            val maxWaitMs = 300_000L
                            val pollIntervalMs = 5_000L
                            val startTime = getTimeMillis()

                            while (true) {
                                val elapsed = getTimeMillis() - startTime
                                if (elapsed > maxWaitMs) {
                                    throw RuntimeException("歌词生成超时，已等待 ${maxWaitMs / 1000} 秒")
                                }

                                val detail = client.getLyricsDetail(taskId)
                                when {
                                    detail?.isSuccess == true -> {
                                        val items = detail.response?.data ?: emptyList()
                                        candidates = items.filter { !it.text.isNullOrBlank() }
                                        statusText = null
                                        // 如果只有一条候选，直接填入
                                        if (candidates.size == 1) {
                                            onLyricsGenerated(candidates.first().text!!)
                                        }
                                        break
                                    }
                                    detail?.isFailed == true -> {
                                        throw RuntimeException(
                                            detail.errorMessage ?: detail.errorCode ?: "歌词生成失败"
                                        )
                                    }
                                    else -> {
                                        statusText = "生成中..."
                                        delay(pollIntervalMs)
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            errorMessage = "❌ ${e.message}"
                            statusText = null
                        } finally {
                            isGenerating = false
                        }
                    }
                },
                glowColor = GlassColors.NeonPurple,
                enabled = prompt.isNotBlank() && !isGenerating
            )

            // 状态文本
            statusText?.let { status ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = status, color = GlassColors.NeonCyan, fontSize = 12.sp)
            }

            // 错误信息 + 重试
            errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = error, color = GlassColors.NeonMagenta, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                GlassButton(
                    text = "🔄 重试",
                    onClick = { errorMessage = null }
                )
            }

            // 多条候选歌词列表
            if (candidates.size > 1) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "🎶 候选歌词（${candidates.size} 条）· 点击选择",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(candidates.size) { index ->
                        val candidate = candidates[index]
                        LyricCandidateItem(
                            index = index + 1,
                            item = candidate,
                            onClick = {
                                candidate.text?.let { onLyricsGenerated(it) }
                            }
                        )
                    }
                }
            }
        }
    }
}

/** 单条候选歌词卡片 */
@Composable
private fun LyricCandidateItem(
    index: Int,
    item: SunoLyricItem,
    onClick: () -> Unit,
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "候选 $index",
                    color = GlassColors.NeonCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                item.title?.let { title ->
                    if (title.isNotBlank()) {
                        Text(
                            text = title,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.text ?: "",
                color = GlassTheme.TextSecondary,
                fontSize = 12.sp,
                maxLines = 6,
                overflow = TextOverflow.Ellipsis
            )
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
