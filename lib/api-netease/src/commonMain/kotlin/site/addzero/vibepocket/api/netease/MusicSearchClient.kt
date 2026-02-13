package site.addzero.vibepocket.api.netease

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.plugins.*
import io.ktor.http.*
import site.addzero.core.network.apiClient

/**
 * 网易云音乐 API 客户端（纯 HTTP 调用层，不含业务逻辑）
 *
 * 直接调用网易云公开 API，无需额外服务端。
 */
object MusicSearchClient {
    var mytoken: String? = null
    init {
        apiClient.config {
            defaultRequest {
                url(BASE_URL)
            }
            defaultRequest {
                headers {
                    mytoken?.let {
                        append(HttpHeaders.Authorization, it)
                    }
                }
            }
        }
    }

    private const val BASE_URL = "https://music.163.com/api/"
    private val music163Ktorfit = Ktorfit.Builder()
        .baseUrl(BASE_URL)
        .httpClient(apiClient)
        .build()

    val musicApi = music163Ktorfit.createNeteaseApi()

    /** 搜索歌曲（委托 musicApi） */
    suspend fun searchSongs(
        keywords: String,
        type: Int = 1,
        limit: Int = 10,
        offset: Int = 0,
    ): NeteaseSearchResponse = musicApi.searchSongs(keywords, type, limit, offset)

    /** 获取歌词（委托 musicApi） */
    suspend fun getLyric(songId: Long): NeteaseLyricResponse = musicApi.getLyric(songId)
}
