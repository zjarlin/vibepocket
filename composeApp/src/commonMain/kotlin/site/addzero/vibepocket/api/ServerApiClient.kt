package site.addzero.vibepocket.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import site.addzero.vibepocket.model.*

/**
 * 内嵌 Server API 客户端（localhost:8080）
 *
 * 封装收藏、历史、Persona 的 CRUD 调用，复用 SettingsPage 中的 HttpClient 模式。
 */
object ServerApiClient {

    private const val BASE_URL = "http://localhost:8080"

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
    }

    // ── 收藏 ─────────────────────────────────────────────────

    suspend fun addFavorite(request: FavoriteRequest): FavoriteItem = try {
        client.post("$BASE_URL/api/favorites") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    } catch (e: Exception) {
        throw RuntimeException("添加收藏失败: ${e.message}", e)
    }

    suspend fun removeFavorite(trackId: String) {
        try {
            client.delete("$BASE_URL/api/favorites/$trackId")
        } catch (e: Exception) {
            throw RuntimeException("取消收藏失败: ${e.message}", e)
        }
    }

    suspend fun getFavorites(): List<FavoriteItem> = try {
        client.get("$BASE_URL/api/favorites").body()
    } catch (_: Exception) {
        emptyList()
    }

    // ── 历史 ─────────────────────────────────────────────────

    suspend fun saveHistory(request: MusicHistorySaveRequest): MusicHistoryItem = try {
        client.post("$BASE_URL/api/suno/history") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    } catch (e: Exception) {
        throw RuntimeException("保存历史失败: ${e.message}", e)
    }

    suspend fun getHistory(): List<MusicHistoryItem> = try {
        client.get("$BASE_URL/api/suno/history").body()
    } catch (_: Exception) {
        emptyList()
    }

    // ── Persona ──────────────────────────────────────────────

    suspend fun savePersona(request: PersonaSaveRequest): PersonaItem = try {
        client.post("$BASE_URL/api/personas") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    } catch (e: Exception) {
        throw RuntimeException("保存 Persona 失败: ${e.message}", e)
    }

    suspend fun getPersonas(): List<PersonaItem> = try {
        client.get("$BASE_URL/api/personas").body()
    } catch (_: Exception) {
        emptyList()
    }
}
