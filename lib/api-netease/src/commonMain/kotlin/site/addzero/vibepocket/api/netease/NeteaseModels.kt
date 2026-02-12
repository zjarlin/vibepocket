package site.addzero.vibepocket.api.netease

import kotlinx.serialization.Serializable

// ── 网易云音乐 API 数据模型 ──────────────────────────────────

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
    val album: NeteaseAlbum? = null,
    val duration: Long = 0,
) {
    val artistNames get() = artists.joinToString(", ") { it.name }
    /** 封面图 URL（优先取专辑封面） */
    val coverUrl: String? get() = album?.picUrl
}

@Serializable
data class NeteaseArtist(
    val id: Long = 0,
    val name: String = "",
    val picUrl: String? = null,
)

@Serializable
data class NeteaseAlbum(
    val id: Long = 0,
    val name: String = "",
    val picUrl: String? = null,
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
