package site.addzero.network.call.qqmusic

import kotlinx.serialization.json.*
import site.addzero.network.call.qqmusic.model.SearchType
import site.addzero.vibepocket.api.music.MusicLyric
import site.addzero.vibepocket.api.music.MusicService
import site.addzero.vibepocket.api.music.MusicTrack

/**
 * QQ 音乐 [MusicService] 实现
 */
class QQMusicService(
    private val qqMusic: QQMusic,
) : MusicService {

    override val platformId: String = "qqmusic"

    override suspend fun searchSongs(keyword: String, limit: Int, offset: Int): List<MusicTrack> {
        val pageNum = if (limit > 0) (offset / limit) + 1 else 1
        val json = qqMusic.search(keyword, SearchType.SONG, resultNum = limit, pageNum = pageNum)
            ?: return emptyList()
        return parseSongList(json)
    }

    override suspend fun getTrackDetail(trackId: String): MusicTrack? {
        // QQ 音乐没有单曲详情公开接口，用搜索模拟
        val json = qqMusic.search(trackId, SearchType.SONG, resultNum = 5)
            ?: return null
        return parseSongList(json).firstOrNull { it.id == trackId }
    }

    override suspend fun getLyric(trackId: String): MusicLyric? {
        val raw = qqMusic.getLyric(trackId)
        if (raw.isBlank()) return null
        // QQMusic.getLyric 返回 lyric + "\n" + trans 拼接
        val parts = raw.split("\n", limit = 2)
        val lrc = parts.getOrNull(0) ?: return null
        val trans = parts.getOrNull(1)?.takeIf { it.isNotBlank() }
        return MusicLyric(lrc = lrc, translatedLrc = trans)
    }

    /**
     * 从 QQ 音乐搜索结果的 song JsonElement 中提取歌曲列表
     *
     * 结构大致为: { "list": [ { "mid": "...", "name": "...", "singer": [...], "album": {...}, ... } ] }
     */
    private fun parseSongList(element: JsonElement): List<MusicTrack> {
        val list = element.jsonObject["list"]?.jsonArray ?: return emptyList()
        return list.mapNotNull { item ->
            val obj = item.jsonObject
            val mid = obj["mid"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
            val name = obj["name"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
            val artist = obj["singer"]?.jsonArray
                ?.mapNotNull { it.jsonObject["name"]?.jsonPrimitive?.contentOrNull }
                ?.joinToString(", ")
                ?: ""
            val albumObj = obj["album"]?.jsonObject
            val albumName = albumObj?.get("name")?.jsonPrimitive?.contentOrNull ?: ""
            val albumMid = albumObj?.get("mid")?.jsonPrimitive?.contentOrNull
            val coverUrl = albumMid?.let { "https://y.gtimg.cn/music/photo_new/T002R300x300M000${it}.jpg" }
            val interval = obj["interval"]?.jsonPrimitive?.longOrNull ?: 0L
            MusicTrack(
                id = mid,
                name = name,
                artist = artist,
                album = albumName,
                coverUrl = coverUrl,
                durationMs = interval * 1000,
                platform = platformId,
            )
        }
    }
}
