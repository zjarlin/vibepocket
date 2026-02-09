package site.addzero.vibepocket.music

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import site.addzero.component.glass.*

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
    onArtistNameChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // 搜索歌词区域
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
                    text = "搜索歌词",
                    onClick = {
                        // TODO: 调用 MusicSearchClient 搜索歌词
                        println("TODO: 搜索歌词 songName=$songName artistName=$artistName")
                    },
                    enabled = songName.isNotBlank()
                )
            }
        }

        // 歌词编辑区域
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
                    color = Color.White.copy(alpha = 0.5f),
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
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 11.sp
                )
            }
        }
    }
}
