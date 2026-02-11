package site.addzero.vibepocket.api

import de.jensklingenberg.ktorfit.http.*
import site.addzero.vibepocket.model.NeteaseLyricResponse
import site.addzero.vibepocket.model.NeteaseSearchResponse

/**
 * 网易云音乐 API 接口定义（Ktorfit 声明式）
 *
 * 纯 HTTP 接口声明，不含业务逻辑。
 */
interface NeteaseApi {

    /** 搜索歌曲 */
    @GET("search/get/web")
    suspend fun searchSongs(
        @Query("s") keywords: String,
        @Query("type") type: Int = 1,
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int = 0,
        @Header("User-Agent") userAgent: String = "Mozilla/5.0",
        @Header("Referer") referer: String = "https://music.163.com/",
    ): NeteaseSearchResponse

    /** 获取歌词 */
    @GET("song/lyric")
    suspend fun getLyric(
        @Query("id") songId: Long,
        @Query("lv") lv: Int = 1,
        @Query("tv") tv: Int = 1,
        @Header("User-Agent") userAgent: String = "Mozilla/5.0",
        @Header("Referer") referer: String = "https://music.163.com/",
    ): NeteaseLyricResponse
}
