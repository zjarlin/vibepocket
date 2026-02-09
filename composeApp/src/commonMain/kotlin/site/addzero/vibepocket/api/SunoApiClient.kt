package site.addzero.vibepocket.api

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.date.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import site.addzero.vibepocket.model.SunoMusicRequest
import site.addzero.vibepocket.model.SunoTask

/**
 * Suno API 客户端（基于 Ktorfit）
 */
class SunoApiClient(
    private val apiToken: String,
    private val baseUrl: String = "https://vector.addzero.site",
) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
    }

    private val api: SunoApi = Ktorfit.Builder()
        .baseUrl(baseUrl.trimEnd('/') + "/")
        .httpClient(client)
        .build()
        .createSunoApi()

    private val authHeader get() = "Bearer $apiToken"

    /** 提交音乐生成任务，返回任务 ID */
    suspend fun generateMusic(request: SunoMusicRequest): String {
        return api.generateMusic(request, authHeader).getOrThrow()
    }

    /** 查询单个任务状态 */
    suspend fun fetchTask(taskId: String): SunoTask? {
        return api.fetchTask(taskId, authHeader).getOrNull()
    }

    /** 轮询等待任务完成 */
    suspend fun waitForCompletion(
        taskId: String,
        maxWaitMs: Long = 600_000L,
        pollIntervalMs: Long = 10_000L,
        onStatusUpdate: ((SunoTask?) -> Unit)? = null,
    ): SunoTask {
        val startTime = getTimeMillis()
        while (getTimeMillis() - startTime < maxWaitMs) {
            val task = fetchTask(taskId)
            onStatusUpdate?.invoke(task)
            when {
                task?.isComplete == true -> return task
                task?.isError == true -> throw RuntimeException(
                    "任务失败: ${task.error ?: task.errorMessage}"
                )
                else -> delay(pollIntervalMs)
            }
        }
        throw RuntimeException("任务超时，已等待 ${maxWaitMs / 1000} 秒")
    }

    fun close() = client.close()
}
