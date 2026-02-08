package site.addzero.vibepocket.music

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import site.addzero.component.glass.*
import site.addzero.vibepocket.model.*

private val prettyJson = Json { prettyPrint = true; encodeDefaults = true }

/**
 * 音乐 Vibe 主界面
 * 分屏布局：左侧分步表单，右侧任务进度
 */
@Composable
fun MusicVibeScreen() {
    // ===== 表单状态 =====
    var currentStep by remember { mutableStateOf(VibeStep.LYRICS) }
    // Step 1: 歌词
    var lyrics by remember { mutableStateOf("") }
    var songName by remember { mutableStateOf("") }
    var artistName by remember { mutableStateOf("") }
    // Step 2: 参数
    var title by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var mv by remember { mutableStateOf("chirp-v5") }
    var makeInstrumental by remember { mutableStateOf(false) }
    var vocalGender by remember { mutableStateOf("m") }
    var negativeTags by remember { mutableStateOf("") }
    var gptDescriptionPrompt by remember { mutableStateOf("") }

    // ===== 任务状态 =====
    var submittedJson by remember { mutableStateOf<String?>(null) }
    var taskStatus by remember { mutableStateOf("未提交") }
    var isSubmitted by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GlassColors.DarkBackground)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // ========== 左侧：分步表单 ==========
            Box(
                modifier = Modifier
                    .weight(if (isSubmitted) 0.5f else 1f)
                    .fillMaxHeight()
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // 标题
                    Text(
                        text = "🎵 Music Vibe",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (currentStep == VibeStep.LYRICS) "第 1 步 / 确认歌词" else "第 2 步 / Vibe 参数",
                        color = GlassColors.NeonCyan,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // 步骤指示器
                    StepIndicator(currentStep)
                    Spacer(modifier = Modifier.height(20.dp))

                    // 表单内容
                    AnimatedContent(targetState = currentStep) { step ->
                        when (step) {
                            VibeStep.LYRICS -> LyricsStep(
                                lyrics = lyrics,
                                onLyricsChange = { lyrics = it },
                                songName = songName,
                                onSongNameChange = { songName = it },
                                artistName = artistName,
                                onArtistNameChange = { artistName = it }
                            )
                            VibeStep.PARAMS -> ParamsStep(
                                title = title,
                                onTitleChange = { title = it },
                                tags = tags,
                                onTagsChange = { tags = it },
                                mv = mv,
                                onMvChange = { mv = it },
                                makeInstrumental = makeInstrumental,
                                onMakeInstrumentalChange = { makeInstrumental = it },
                                vocalGender = vocalGender,
                                onVocalGenderChange = { vocalGender = it },
                                negativeTags = negativeTags,
                                onNegativeTagsChange = { negativeTags = it },
                                gptDescriptionPrompt = gptDescriptionPrompt,
                                onGptDescriptionPromptChange = { gptDescriptionPrompt = it }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 底部按钮
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (currentStep == VibeStep.PARAMS) {
                            GlassButton(
                                text = "← 上一步",
                                onClick = { currentStep = VibeStep.LYRICS }
                            )
                        }

                        when (currentStep) {
                            VibeStep.LYRICS -> {
                                NeonGlassButton(
                                    text = "下一步 →",
                                    onClick = { currentStep = VibeStep.PARAMS },
                                    glowColor = GlassColors.NeonCyan,
                                    enabled = lyrics.isNotBlank()
                                )
                            }
                            VibeStep.PARAMS -> {
                                NeonGlassButton(
                                    text = "🚀 提交 Vibe",
                                    onClick = {
                                        val request = SunoMusicRequest(
                                            mv = mv,
                                            title = title.ifBlank { null },
                                            tags = tags.ifBlank { null },
                                            prompt = lyrics,
                                            makeInstrumental = makeInstrumental,
                                            vocalGender = vocalGender,
                                            negativeTags = negativeTags.ifBlank { null },
                                            gptDescriptionPrompt = gptDescriptionPrompt.ifBlank { null }
                                        )
                                        val jsonStr = prettyJson.encodeToString(request)
                                        submittedJson = jsonStr
                                        taskStatus = "已提交，等待后端处理..."
                                        isSubmitted = true
                                        // TODO: 实际调用后端 API
                                        println("===== Suno Request JSON =====")
                                        println(jsonStr)
                                        println("=============================")
                                    },
                                    glowColor = GlassColors.NeonPurple
                                )
                            }
                        }
                    }
                }
            }

            // ========== 右侧：任务进度面板 ==========
            if (isSubmitted) {
                Box(
                    modifier = Modifier
                        .weight(0.5f)
                        .fillMaxHeight()
                        .padding(top = 24.dp, end = 24.dp, bottom = 24.dp)
                ) {
                    TaskProgressPanel(
                        submittedJson = submittedJson,
                        taskStatus = taskStatus
                    )
                }
            }
        }
    }
}
