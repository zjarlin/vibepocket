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
import site.addzero.vibepocket.model.SunoTask

@Composable
fun TaskProgressPanel(
    submittedJson: String?,
    taskStatus: String,
    sunoTask: SunoTask? = null,
) {
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
            Text(
                text = "📊 任务面板",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GlassStatCard(
                    value = if (sunoTask != null) "1" else "0",
                    label = "任务数",
                    modifier = Modifier.width(100.dp).height(80.dp),
                    glowColor = GlassColors.NeonCyan
                )
                GlassStatCard(
                    value = sunoTask?.displayStatus?.take(4) ?: taskStatus.take(4),
                    label = "状态",
                    modifier = Modifier.width(100.dp).height(80.dp),
                    glowColor = when {
                        sunoTask?.isComplete == true -> GlassColors.NeonCyan
                        sunoTask?.isError == true -> GlassColors.NeonMagenta
                        else -> GlassColors.NeonPurple
                    }
                )
                // 显示时长（如果有）
                if (sunoTask?.duration != null) {
                    GlassStatCard(
                        value = "${sunoTask.duration!!.toInt()}s",
                        label = "时长",
                        modifier = Modifier.width(100.dp).height(80.dp),
                        glowColor = GlassColors.NeonCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            GlassInfoCard(
                title = "当前状态",
                content = taskStatus,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ===== 生成结果 =====
            Text("🎵 生成结果", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            if (sunoTask?.isComplete == true && sunoTask.audioUrl != null) {
                // 任务完成，展示结果
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        sunoTask.title?.let {
                            Text("🎵 $it", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        }
                        sunoTask.tags?.let {
                            Text("🏷️ $it", color = GlassTheme.TextSecondary, fontSize = 12.sp)
                        }
                        Text(
                            text = "🔗 ${sunoTask.audioUrl}",
                            color = GlassColors.NeonCyan,
                            fontSize = 12.sp,
                        )
                        sunoTask.videoUrl?.let { url ->
                            Text(
                                text = "🎬 $url",
                                color = GlassColors.NeonPurple,
                                fontSize = 12.sp,
                            )
                        }
                    }
                }
            } else if (sunoTask?.isError == true) {
                // 任务失败
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "❌ ${sunoTask.error ?: sunoTask.errorMessage ?: "未知错误"}",
                            color = GlassColors.NeonMagenta,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                // 等待中
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (sunoTask?.isProcessing == true) "⏳ 正在生成中，请稍候..."
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
