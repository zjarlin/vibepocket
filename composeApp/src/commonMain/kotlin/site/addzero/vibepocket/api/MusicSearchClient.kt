package site.addzero.vibepocket.api

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import site.addzero.vibepocket.model.*

/**
 * 网易云音乐 API 客户端（纯 HTTP 调用层，不含业务逻辑）
 *
 * 直接调用网易云公开 API，无需额外服务端。
 */
object MusicSearchClient {

    private const val BASE_URL = "https://music.163.com/api/"

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    private val api: NeteaseApi = Ktorfit.Builder()
        .baseUrl(BASE_URL)
        .httpClient(client)
        .build()
        .createNeteaseApi()

    /** 搜索歌曲 */
    suspend fun searchSongs(
        keywords: String,
        limit: Int = 10,
        offset: Int = 0,
    ): NeteaseSearchResponse {
        return api.searchSongs(keywords = keywords, limit = limit, offset = offset)
    }

    /** 获取歌词 */
    suspend fun getLyric(songId: Long): NeteaseLyricResponse {
        return api.getLyric(songId = songId)
    }
}
