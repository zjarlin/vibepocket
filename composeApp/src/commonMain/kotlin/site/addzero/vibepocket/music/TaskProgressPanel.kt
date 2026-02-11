package site.addzero.vibepocket.music

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import site.addzero.component.glass.*
import site.addzero.vibepocket.model.SunoTaskDetail

@Composable
fun TaskProgressPanel(
    submittedJson: String?,
    taskStatus: String,
    taskDetail: SunoTaskDetail? = null,
) {
    val tracks = taskDetail?.response?.sunoData ?: emptyList()

    NeonGlassCard(
        modifier = Modifier.fillMaxSize(),
        glowColor = GlassColors.NeonMagenta
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("📊 任务面板", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GlassStatCard(
                    value = "${tracks.size}",
                    label = "音轨数",
                    modifier = Modifier.width(100.dp).height(80.dp),
                    glowColor = GlassColors.NeonCyan
                )
                GlassStatCard(
                    value = taskDetail?.displayStatus?.take(4) ?: taskStatus.take(4),
                    label = "状态",
                    modifier = Modifier.width(100.dp).height(80.dp),
                    glowColor = when {
                        taskDetail?.isSuccess == true -> GlassColors.NeonCyan
                        taskDetail?.isFailed == true -> GlassColors.NeonMagenta
                        else -> GlassColors.NeonPurple
                    }
                )
                val firstDuration = taskDetail?.firstTrack?.duration
                if (firstDuration != null) {
                    GlassStatCard(
                        value = "${firstDuration.toInt()}s",
                        label = "时长",
                        modifier = Modifier.width(100.dp).height(80.dp),
                        glowColor = GlassColors.NeonCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            GlassInfoCard(title = "当前状态", content = taskStatus, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))

            // ===== 生成结果 =====
            Text("🎵 生成结果", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            if (taskDetail?.isSuccess == true && tracks.isNotEmpty()) {
                tracks.forEach { track ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            track.title?.let {
                                Text("🎵 $it", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            }
                            track.tags?.let {
                                Text("🏷️ $it", color = GlassTheme.TextSecondary, fontSize = 12.sp)
                            }
                            track.audioUrl?.let { url ->
                                Text("🔗 $url", color = GlassColors.NeonCyan, fontSize = 12.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else if (taskDetail?.isFailed == true) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "❌ ${taskDetail.errorMessage ?: taskDetail.errorCode ?: "未知错误"}",
                            color = GlassColors.NeonMagenta,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (taskDetail?.isProcessing == true) "⏳ 正在生成中，请稍候..."
                            else "等待提交...",
                            color = GlassTheme.TextTertiary,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ===== 请求 JSON =====
            Text("📋 请求 JSON", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            submittedJson?.let { json ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(12.dp)
                        .horizontalScroll(rememberScrollState())
                ) {
                    Text(
                        text = json,
                        color = GlassColors.NeonCyan.copy(alpha = 0.9f),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
