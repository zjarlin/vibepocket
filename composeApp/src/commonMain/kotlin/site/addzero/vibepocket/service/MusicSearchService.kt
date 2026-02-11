package site.addzero.vibepocket.service

import site.addzero.vibepocket.api.MusicSearchClient
import site.addzero.vibepocket.model.NeteaseSearchSong

/**
 * 音乐搜索业务层
 *
 * 封装搜索、歌词获取等业务逻辑，UI 层只依赖 Service 而非直接调 API。
 */
object MusicSearchService {

    /**
     * 根据歌名（+歌手）搜索歌曲，返回全部结果（含封面、歌手等信息）
     */
    suspend fun searchSongs(
        songName: String,
        artistName: String? = null,
        limit: Int = 20,
    ): List<NeteaseSearchSong> {
        val keywords = if (artistName.isNullOrBlank()) songName else "$songName $artistName"
        val resp = MusicSearchClient.searchSongs(keywords = keywords, limit = limit)
        val songs = resp.result?.songs ?: emptyList()

        // 如果指定了歌手，优先展示匹配的，但不丢弃其他结果
        if (!artistName.isNullOrBlank()) {
            val (matched, rest) = songs.partition { song ->
                song.artists.any { it.name.contains(artistName, ignoreCase = true) }
            }
            return matched + rest
        }
        return songs
    }

    /**
     * 获取指定歌曲的歌词文本
     *
     * @return 歌词文本，无歌词时返回 null
     */
    suspend fun getLyric(songId: Long): String? {
        val resp = MusicSearchClient.getLyric(songId)
        return resp.lrc?.lyric
    }

    /**
     * 兼容旧接口：根据歌名搜索并取第一首的歌词
     */
    suspend fun getLyricBySongName(
        songName: String,
        artistName: String? = null,
    ): String? {
        val songs = searchSongs(songName, artistName)
        if (songs.isEmpty()) return null
        return getLyric(songs.first().id)
    }
}
