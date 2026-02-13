package site.addzero.vibepocket.api.netease

import de.jensklingenberg.ktorfit.http.*

/**
 * 网易云音乐 API 接口定义（Ktorfit 声明式）
 *
 * 纯 HTTP 接口声明，不含业务逻辑。
 * type: 1=歌曲, 10=专辑, 100=歌手, 1000=歌单, 1006=歌词
 */
interface NeteaseApi {

    /** 通用搜索（按 type 区分搜索类型） */
    @GET("search/get/web")
    suspend fun search(
        @Query("s") keywords: String,
        @Query("type") type: Int = 1,
        @Query("limit") limit: Int = 30,
        @Query("offset") offset: Int = 0,
        @Header("User-Agent") userAgent: String = UA,
        @Header("Referer") referer: String = REFERER,
    ): NeteaseSearchResponse

    /** 搜索歌曲（type=1） */
    @GET("search/get/web")
    suspend fun searchSongs(
        @Query("s") keywords: String,
        @Query("type") type: Int = 1,
        @Query("limit") limit: Int = 30,
        @Query("offset") offset: Int = 0,
        @Header("User-Agent") userAgent: String = UA,
        @Header("Referer") referer: String = REFERER,
    ): NeteaseSearchResponse

    /** 搜索歌手（type=100） */
    @GET("search/get/web")
    suspend fun searchArtists(
        @Query("s") keywords: String,
        @Query("type") type: Int = 100,
        @Query("limit") limit: Int = 30,
        @Query("offset") offset: Int = 0,
        @Header("User-Agent") userAgent: String = UA,
        @Header("Referer") referer: String = REFERER,
    ): NeteaseSearchResponse

    /** 搜索专辑（type=10） */
    @GET("search/get/web")
    suspend fun searchAlbums(
        @Query("s") keywords: String,
        @Query("type") type: Int = 10,
        @Query("limit") limit: Int = 30,
        @Query("offset") offset: Int = 0,
        @Header("User-Agent") userAgent: String = UA,
        @Header("Referer") referer: String = REFERER,
    ): NeteaseSearchResponse

    /** 搜索歌单（type=1000） */
    @GET("search/get/web")
    suspend fun searchPlaylists(
        @Query("s") keywords: String,
        @Query("type") type: Int = 1000,
        @Query("limit") limit: Int = 30,
        @Query("offset") offset: Int = 0,
        @Header("User-Agent") userAgent: String = UA,
        @Header("Referer") referer: String = REFERER,
    ): NeteaseSearchResponse

    /** 根据歌词内容模糊搜索歌曲（type=1006） */
    @GET("search/get/web")
    suspend fun searchByLyric(
        @Query("s") lyricKeywords: String,
        @Query("type") type: Int = 1006,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Header("User-Agent") userAgent: String = UA,
        @Header("Referer") referer: String = REFERER,
    ): NeteaseLyricSearchResponse

    /** 获取歌词 */
    @GET("song/lyric")
    suspend fun getLyric(
        @Query("id") songId: Long,
        @Query("lv") lv: Int = 1,
        @Query("tv") tv: Int = 1,
        @Header("User-Agent") userAgent: String = UA,
        @Header("Referer") referer: String = REFERER,
    ): NeteaseLyricResponse

    /** 获取歌曲详情 */
    @GET("song/detail")
    suspend fun getSongDetail(
        @Query("ids") ids: String,
        @Header("User-Agent") userAgent: String = UA,
        @Header("Referer") referer: String = REFERER,
    ): SongDetailResponse

    companion object {
        const val UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
        const val REFERER = "https://music.163.com/"
    }
}
