package site.addzero.vibepocket.api.netease

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.plugins.*
import io.ktor.http.*
import site.addzero.core.network.apiClient

/**
 * 网易云音乐 API 客户端
 *
 * HTTP 层由 Ktorfit 声明式接口 [NeteaseApi] 处理，
 * 本 object 提供业务便捷方法（歌名+歌手搜索、歌词片段搜索等）。
 */
object MusicSearchClient {
    private const val BASE_URL = "https://music.163.com/api/"

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

    private val music163Ktorfit = Ktorfit.Builder()
        .baseUrl(BASE_URL)
        .httpClient(apiClient)
        .build()

    val musicApi: NeteaseApi = music163Ktorfit.createNeteaseApi()

    // ── 业务便捷方法 ────────────────────────────────────────

    /** 搜索歌曲 */
    suspend fun searchSongs(keywords: String, limit: Int = 30, offset: Int = 0): List<NeteaseSearchSong> {
        val resp = musicApi.searchSongs(keywords, limit = limit, offset = offset)
        return resp.result?.songs ?: emptyList()
    }

    /** 搜索歌手 */
    suspend fun searchArtists(keywords: String, limit: Int = 30, offset: Int = 0): List<NeteaseArtist> {
        val resp = musicApi.searchArtists(keywords, limit = limit, offset = offset)
        return resp.result?.artists ?: emptyList()
    }

    /** 搜索专辑 */
    suspend fun searchAlbums(keywords: String, limit: Int = 30, offset: Int = 0): List<NeteaseAlbum> {
        val resp = musicApi.searchAlbums(keywords, limit = limit, offset = offset)
        return resp.result?.albums ?: emptyList()
    }

    /** 搜索歌单 */
    suspend fun searchPlaylists(keywords: String, limit: Int = 30, offset: Int = 0): List<NeteasePlaylist> {
        val resp = musicApi.searchPlaylists(keywords, limit = limit, offset = offset)
        return resp.result?.playlists ?: emptyList()
    }

    /** 根据歌词片段搜索歌曲 */
    suspend fun searchByLyric(lyricFragment: String, limit: Int = 20): List<NeteaseLyricSearchSong> {
        val resp = musicApi.searchByLyric(lyricFragment, limit = limit)
        return resp.result?.songs ?: emptyList()
    }

    /** 获取歌词 */
    suspend fun getLyric(songId: Long): NeteaseLyricResponse {
        return musicApi.getLyric(songId)
    }

    /** 获取歌曲详情 */
    suspend fun getSongDetail(songIds: List<Long>): List<NeteaseSearchSong> {
        val ids = "[${songIds.joinToString(",")}]"
        val resp = musicApi.getSongDetail(ids)
        return resp.songs ?: emptyList()
    }

    /**
     * 根据歌名和歌手搜索歌曲
     *
     * @param songName 歌名
     * @param artistName 歌手名（可选，用于二次过滤）
     */
    suspend fun searchBySongAndArtist(songName: String, artistName: String? = null): List<NeteaseSearchSong> {
        val keywords = if (artistName != null) "$songName $artistName" else songName
        val songs = searchSongs(keywords, limit = 10)
        return if (artistName != null) {
            songs.filter { song ->
                song.artists.any { it.name.contains(artistName, ignoreCase = true) }
            }
        } else {
            songs
        }
    }

    /**
     * 根据歌名获取歌词
     *
     * @param songName 歌名
     * @param artistName 歌手名（可选，用于精确匹配）
     * @return 歌词响应，找不到返回 null
     */
    suspend fun getLyricBySongName(songName: String, artistName: String? = null): NeteaseLyricResponse? {
        val songs = searchBySongAndArtist(songName, artistName)
        if (songs.isEmpty()) return null
        return getLyric(songs.first().id)
    }

    /**
     * 根据歌词片段获取完整歌词
     *
     * @param lyricFragment 歌词片段
     * @param limit 返回数量限制
     * @param filterEmpty 是否过滤空歌词
     * @return 歌曲与歌词组合列表
     */
    suspend fun getLyricsByFragment(
        lyricFragment: String,
        limit: Int = 5,
        filterEmpty: Boolean = true,
    ): List<SongWithLyric> {
        val songs = searchByLyric(lyricFragment, limit = limit)
        return songs.mapNotNull { lyricSong ->
            try {
                val lyric = getLyric(lyricSong.id)
                if (filterEmpty && lyric.lrc?.lyric.isNullOrBlank()) {
                    null
                } else {
                    // 转为通用 Song 类型
                    val song = NeteaseSearchSong(
                        id = lyricSong.id,
                        name = lyricSong.name,
                        artists = lyricSong.artists,
                        album = lyricSong.album,
                        duration = lyricSong.duration,
                    )
                    SongWithLyric(song, lyric)
                }
            } catch (_: Exception) {
                null
            }
        }
    }
}
