package site.addzero.vibepocket.api

import de.jensklingenberg.ktorfit.http.*
import site.addzero.vibepocket.model.*

/**
 * Server API 接口定义 - ktorfit 自动生成实现
 */
interface ServerApi {

    // ── 收藏 ─────────────────────────────────────────────────

    @POST("api/favorites")
    suspend fun addFavorite(@Body request: FavoriteRequest): FavoriteItem

    @DELETE("api/favorites/{trackId}")
    suspend fun removeFavorite(@Path("trackId") trackId: String)

    @GET("api/favorites")
    suspend fun getFavorites(): List<FavoriteItem>

    // ── 历史 ─────────────────────────────────────────────────

    @POST("api/suno/history")
    suspend fun saveHistory(@Body request: MusicHistorySaveRequest): MusicHistoryItem

    @GET("api/suno/history")
    suspend fun getHistory(): List<MusicHistoryItem>

    // ── Persona ──────────────────────────────────────────────

    @POST("api/personas")
    suspend fun savePersona(@Body request: PersonaSaveRequest): PersonaItem

    @GET("api/personas")
    suspend fun getPersonas(): List<PersonaItem>
}
