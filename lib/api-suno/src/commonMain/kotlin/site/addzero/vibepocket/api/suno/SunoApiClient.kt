package site.addzero.vibepocket.api.suno

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.util.date.*
import kotlinx.coroutines.delay
import site.addzero.core.network.apiClient

/**
 * Suno API 客户端（全量接口）
 *
 * 对接 https://api.sunoapi.org/api/v1
 */
class SunoApiClient(
    private val apiToken: String,
    baseUrl: String = "https://api.sunoapi.org/api/v1",
) {

    val sunoKtorfit = Ktorfit.Builder()
        .baseUrl(baseUrl.trimEnd('/') + "/")
        .httpClient(apiClient)
        .build()
    private val api = sunoKtorfit.createSunoApi()

    private val auth get() = "Bearer $apiToken"

    // ── 音乐生成 ─────────────────────────────────────────────

    suspend fun generateMusic(request: SunoGenerateRequest): String =
        api.generateMusic(request, auth).getOrThrow().taskId

    suspend fun extendMusic(request: SunoExtendRequest): String =
        api.extendMusic(request, auth).getOrThrow().taskId

    suspend fun uploadCover(request: SunoUploadCoverRequest): String =
        api.uploadCover(request, auth).getOrThrow().taskId

    suspend fun uploadExtend(request: SunoUploadExtendRequest): String =
        api.uploadExtend(request, auth).getOrThrow().taskId

    suspend fun addVocals(request: SunoAddVocalsRequest): String =
        api.addVocals(request, auth).getOrThrow().taskId

    suspend fun addInstrumental(request: SunoAddInstrumentalRequest): String =
        api.addInstrumental(request, auth).getOrThrow().taskId

    suspend fun generateMusicCover(request: SunoMusicCoverRequest): String =
        api.generateMusicCover(request, auth).getOrThrow().taskId

    suspend fun replaceSection(request: SunoReplaceSectionRequest): String =
        api.replaceSection(request, auth).getOrThrow().taskId

    // ── 查询 ─────────────────────────────────────────────────

    suspend fun getTaskDetail(taskId: String): SunoTaskDetail? =
        api.getTaskDetail(taskId, auth).getOrNull()

    suspend fun getCoverDetail(taskId: String): SunoTaskDetail? =
        api.getCoverDetail(taskId, auth).getOrNull()

    // ── 歌词 ─────────────────────────────────────────────────

    suspend fun generateLyrics(request: SunoLyricsRequest): String =
        api.generateLyrics(request, auth).getOrThrow().taskId

    suspend fun getLyricsDetail(taskId: String): SunoLyricsTaskDetail? =
        api.getLyricsDetail(taskId, auth).getOrNull()

    suspend fun getTimestampedLyrics(request: SunoTimestampedLyricsRequest): SunoTimestampedLyricsData =
        api.getTimestampedLyrics(request, auth).getOrThrow()

    // ── Persona ──────────────────────────────────────────────

    suspend fun generatePersona(request: SunoGeneratePersonaRequest): SunoPersonaData =
        api.generatePersona(request, auth).getOrThrow()

    // ── 音频处理 ─────────────────────────────────────────────

    suspend fun vocalRemoval(request: SunoVocalRemovalRequest): String =
        api.vocalRemoval(request, auth).getOrThrow().taskId

    suspend fun boostMusicStyle(request: SunoBoostStyleRequest): SunoBoostStyleData =
        api.boostMusicStyle(request, auth).getOrThrow()

    suspend fun convertToWav(request: SunoWavRequest): String =
        api.convertToWav(request, auth).getOrThrow().taskId

    // ── 账户 ─────────────────────────────────────────────────

    suspend fun getCredits(): Int =
        api.getCredits(auth).getOrThrow().credits

    // ── 轮询 ─────────────────────────────────────────────────

    /** 轮询等待音乐生成任务完成 */
    suspend fun waitForCompletion(
        taskId: String,
        maxWaitMs: Long = 600_000L,
        pollIntervalMs: Long = 30_000L,
        onStatusUpdate: ((SunoTaskDetail?) -> Unit)? = null,
    ): SunoTaskDetail {
        val startTime = getTimeMillis()
        while (getTimeMillis() - startTime < maxWaitMs) {
            val detail = getTaskDetail(taskId)
            onStatusUpdate?.invoke(detail)
            when {
                detail?.isSuccess == true -> return detail
                detail?.isFailed == true -> throw RuntimeException(
                    "任务失败: ${detail.errorMessage ?: detail.errorCode ?: "未知错误"}"
                )
                else -> delay(pollIntervalMs)
            }
        }
        throw RuntimeException("任务超时，已等待 ${maxWaitMs / 1000} 秒")
    }
}
