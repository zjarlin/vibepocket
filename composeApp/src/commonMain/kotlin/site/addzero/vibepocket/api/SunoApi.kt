package site.addzero.vibepocket.api

import de.jensklingenberg.ktorfit.http.*
import site.addzero.vibepocket.model.SunoMusicRequest
import site.addzero.vibepocket.model.SunoTask

/**
 * Suno API 接口定义（Ktorfit 声明式）
 */
interface SunoApi {

    /**
     * 提交音乐生成任务
     * @return 任务 ID（包装在 ApiResult 中）
     */
    @POST("suno/submit/music")
    suspend fun generateMusic(
        @Body request: SunoMusicRequest,
        @Header("Authorization") auth: String,
    ): ApiResult<String>

    /**
     * 查询单个任务状态
     */
    @GET("suno/fetch/{taskId}")
    suspend fun fetchTask(
        @Path("taskId") taskId: String,
        @Header("Authorization") auth: String,
    ): ApiResult<SunoTask>
}
