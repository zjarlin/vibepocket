package site.addzero.vibepocket.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Suno 音乐生成请求 (对应 tool-api-suno 的 SunoMusicRequest)
 */
@Serializable
data class SunoMusicRequest(
    val mv: String = "chirp-v5",
    @SerialName("gpt_description_prompt")
    val gptDescriptionPrompt: String? = null,
    @SerialName("notify_hook")
    val notifyHook: String? = null,
    val title: String? = null,
    val tags: String? = null,
    val prompt: String,
    @SerialName("make_instrumental")
    val makeInstrumental: Boolean? = false,
    @SerialName("task_id")
    val taskId: String? = null,
    @SerialName("continue_clip_id")
    val continueClipId: String? = null,
    @SerialName("continue_at")
    val continueAt: Int? = null,
    @SerialName("persona_id")
    val personaId: String? = null,
    @SerialName("artist_clip_id")
    val artistClipId: String? = null,
    @SerialName("vocal_gender")
    val vocalGender: String? = "m",
    @SerialName("generation_type")
    val generationType: String? = null,
    @SerialName("negative_tags")
    val negativeTags: String? = null,
    @SerialName("clip_id")
    val clipId: String? = null,
    @SerialName("is_infill")
    val isInfill: Boolean? = null,
    val task: String? = "extend"
)

/**
 * Suno 任务信息 (对应 tool-api-suno 的 SunoTask)
 */
@Serializable
data class SunoTask(
    val id: String? = null,
    val status: String? = null,
    val prompt: String? = null,
    @SerialName("gpt_description_prompt")
    val gptDescriptionPrompt: String? = null,
    val title: String? = null,
    val tags: String? = null,
    val mv: String? = null,
    val type: String? = null,
    val duration: Double? = null,
    @SerialName("audio_url")
    val audioUrl: String? = null,
    @SerialName("video_url")
    val videoUrl: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("error_message")
    val errorMessage: String? = null,
    val error: String? = null,
    @SerialName("clip_id")
    val clipId: String? = null,
    val instrumental: Boolean? = null
) {
    val isComplete get() = status == "complete" || status == "streaming"
    val isError get() = status == "error"
    val isProcessing get() = status == "processing" || status == "queued"
    val displayStatus: String
        get() = when (status) {
            "queued" -> "排队中..."
            "processing" -> "生成中..."
            "complete" -> "已完成 ✓"
            "streaming" -> "流式完成 ✓"
            "error" -> "失败 ✗"
            else -> status ?: "未知"
        }
}

/** Vibe 表单步骤 */
enum class VibeStep {
    LYRICS,  // 第一步：确认歌词
    PARAMS   // 第二步：填写生成参数
}

/** 模型版本选项 */
val MODEL_VERSIONS = listOf("chirp-v5", "chirp-v4-tau", "chirp-v4", "chirp-v3.5")

/** 性别选项 */
val VOCAL_GENDERS = listOf("m" to "男声", "f" to "女声")
