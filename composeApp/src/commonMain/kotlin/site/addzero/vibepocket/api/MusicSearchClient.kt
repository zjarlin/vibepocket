package site.addzero.vibepocket.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// ── Model ───────────────────────────────────────────────────

@Serializable
data class NeteaseSearchResponse(
    val code: Int = 0,
    val result: NeteaseSearchResult? = null,
)

@Serializable
data class NeteaseSearchResult(
    val songs: List<NeteaseSearchSong>? = null,
    val songCount: Int? = null,
)

@Serializable
data class NeteaseSearchSong(
    val id: Long,
    val name: String,
    val artists: List<NeteaseArtist> = emptyList(),
) {
    val artistNames get() = artists.joinToString(", ") { it.name }
}

@Serializable
data class NeteaseArtist(
    val id: Long = 0,
    val name: String = "",
)

@Serializable
data class NeteaseLyricResponse(
    val code: Int = 0,
    val lrc: NeteaseLrc? = null,
    val tlyric: NeteaseLrc? = null,
)

@Serializable
data class NeteaseLrc(
    val lyric: String? = null,
)

// ── Client ──────────────────────────────────────────────────

/**
 * 网易云音乐搜索客户端（Ktor 实现，commonMain 全平台可用）
 *
 * 直接调用网易云公开 API，无需额外服务端。
 */
object MusicSearchClient {

    private const val BASE_URL = "https://music.163.com/api"

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    /**
     * 搜索歌曲
     */
    suspend fun searchSongs(
        keywords: String,
        limit: Int = 10,
        offset: Int = 0,
    ): List<NeteaseSearchSong> {
        val response = client.get("$BASE_URL/search/get/web") {
            parameter("s", keywords)
            parameter("type", 1) // 1 = 歌曲
            parameter("limit", limit)
            parameter("offset", offset)
            header("User-Agent", "Mozilla/5.0")
            header("Referer", "https://music.163.com/")
        }
        val result = response.body<NeteaseSearchResponse>()
        return result.result?.songs ?: emptyList()
    }

    /**
     * 根据歌名（+歌手）搜索歌曲
     */
    suspend fun searchBySongAndArtist(
        songName: String,
        artistName: String? = null,
    ): List<NeteaseSearchSong> {
        val keywords = if (artistName.isNullOrBlank()) songName else "$songName $artistName"
        val songs = searchSongs(keywords)
        // 如果指定了歌手，二次过滤
        return if (!artistName.isNullOrBlank()) {
            songs.filter { song ->
                song.artists.any { it.name.contains(artistName, ignoreCase = true) }
            }.ifEmpty { songs } // 过滤后为空则返回全部
        } else {
            songs
        }
    }

    /**
     * 获取歌词
     */
    suspend fun getLyric(songId: Long): NeteaseLyricResponse {
        val response = client.get("$BASE_URL/song/lyric") {
            parameter("id", songId)
            parameter("lv", 1)
            parameter("tv", 1)
            header("User-Agent", "Mozilla/5.0")
            header("Referer", "https://music.163.com/")
        }
        return response.body<NeteaseLyricResponse>()
    }

    /**
     * 根据歌名获取歌词（搜索 + 取第一首的歌词）
     */
    suspend fun getLyricBySongName(
        songName: String,
        artistName: String? = null,
    ): String? {
        val songs = searchBySongAndArtist(songName, artistName)
        if (songs.isEmpty()) return null
        val lyricResp = getLyric(songs.first().id)
        return lyricResp.lrc?.lyric
    }
}
