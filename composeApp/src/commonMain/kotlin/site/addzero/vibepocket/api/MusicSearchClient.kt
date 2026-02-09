package site.addzero.vibepocket.api

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
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

// ── Client（基于 Ktorfit）───────────────────────────────────

/**
 * 网易云音乐搜索客户端（Ktorfit 声明式实现，commonMain 全平台可用）
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
    ): List<NeteaseSearchSong> {
        val result = api.searchSongs(keywords = keywords, limit = limit, offset = offset)
        return result.result?.songs ?: emptyList()
    }

    /** 根据歌名（+歌手）搜索歌曲 */
    suspend fun searchBySongAndArtist(
        songName: String,
        artistName: String? = null,
    ): List<NeteaseSearchSong> {
        val keywords = if (artistName.isNullOrBlank()) songName else "$songName $artistName"
        val songs = searchSongs(keywords)
        return if (!artistName.isNullOrBlank()) {
            songs.filter { song ->
                song.artists.any { it.name.contains(artistName, ignoreCase = true) }
            }.ifEmpty { songs }
        } else {
            songs
        }
    }

    /** 获取歌词 */
    suspend fun getLyric(songId: Long): NeteaseLyricResponse {
        return api.getLyric(songId = songId)
    }

    /** 根据歌名获取歌词（搜索 + 取第一首的歌词） */
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
