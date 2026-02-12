package site.addzero.vibepocket.api

import site.addzero.vibepocket.model.*

/**
 * 内嵌 Server API 客户端（localhost:8080）
 *
 * 封装收藏、历史、Persona 的 CRUD 调用，使用 ktorfit 自动生成的实现。
 */
object ServerApiClient {

    private const val BASE_URL = "http://localhost:8080"

    // ktorfit 生成的实现，需要在实际使用时初始化
    private lateinit var apiService: ServerApi

    /**
     * 初始化 API 服务
     */
    fun initialize(api: ServerApi) {
        apiService = api
    }

    // ── 收藏 ─────────────────────────────────────────────────

    suspend fun addFavorite(request: FavoriteRequest): FavoriteItem = try {
        apiService.addFavorite(request)
    } catch (e: Exception) {
        throw RuntimeException("添加收藏失败: ${e.message}", e)
    }

    suspend fun removeFavorite(trackId: String) {
        try {
            apiService.removeFavorite(trackId)
        } catch (e: Exception) {
            throw RuntimeException("取消收藏失败: ${e.message}", e)
        }
    }

    suspend fun getFavorites(): List<FavoriteItem> = try {
        apiService.getFavorites()
    } catch (_: Exception) {
        emptyList()
    }

    // ── 历史 ─────────────────────────────────────────────────

    suspend fun saveHistory(request: MusicHistorySaveRequest): MusicHistoryItem = try {
        apiService.saveHistory(request)
    } catch (e: Exception) {
        throw RuntimeException("保存历史失败: ${e.message}", e)
    }

    suspend fun getHistory(): List<MusicHistoryItem> = try {
        apiService.getHistory()
    } catch (_: Exception) {
        emptyList()
    }

    // ── Persona ──────────────────────────────────────────────

    suspend fun savePersona(request: PersonaSaveRequest): PersonaItem = try {
        apiService.savePersona(request)
    } catch (e: Exception) {
        throw RuntimeException("保存 Persona 失败: ${e.message}", e)
    }

    suspend fun getPersonas(): List<PersonaItem> = try {
        apiService.getPersonas()
    } catch (_: Exception) {
        emptyList()
    }
}
