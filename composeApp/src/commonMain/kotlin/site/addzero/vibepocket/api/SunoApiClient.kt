package site.addzero.vibepocket.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.date.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import site.addzero.vibepocket.model.SunoMusicRequest
import site.addzero.vibepocket.model.SunoTask

/**
 * Suno API 客户端（Ktor 实现，commonMain 全平台可用）
 *
 * 对接 VectorEngine Suno API，提供音乐生成、任务查询、轮询等待等功能。
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

    /**
     * 提交音乐生成任务
     * @return 任务 ID
     */
    suspend fun generateMusic(request: SunoMusicRequest): String {
        val response = client.post("$baseUrl/suno/submit/music") {
            contentType(ContentType.Application.Json)
            bearerAuth(apiToken)
            setBody(request)
        }
        val result = response.body<ApiResult<String>>()
        return result.getOrThrow()
    }

    /**
     * 查询单个任务状态
     */
    suspend fun fetchTask(taskId: String): SunoTask? {
        val response = client.get("$baseUrl/suno/fetch/$taskId") {
            bearerAuth(apiToken)
            accept(ContentType.Application.Json)
        }
        val result = response.body<ApiResult<SunoTask>>()
        return result.getOrNull()
    }

    /**
     * 轮询等待任务完成
     *
     * @param taskId 任务 ID
     * @param maxWaitMs 最长等待毫秒数，默认 10 分钟
     * @param pollIntervalMs 轮询间隔毫秒数，默认 10 秒
     * @param onStatusUpdate 状态更新回调
     * @return 完成的任务
     */
    suspend fun waitForCompletion(
        taskId: String,
        maxWaitMs: Long = 600_000L,
        pollIntervalMs: Long = 10_000L,
        onStatusUpdate: ((SunoTask?) -> Unit)? = null,
    ): SunoTask {
        val startTime = currentTimeMillis()

        while (currentTimeMillis() - startTime < maxWaitMs) {
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

    fun close() {
        client.close()
    }
}

/** 跨平台获取当前时间戳 */
private fun currentTimeMillis(): Long = getTimeMillis()
