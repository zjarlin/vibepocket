package site.addzero.vibepocket.routes

import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import site.addzero.ioc.annotation.Bean
import site.addzero.network.call.music.MusicSearchClient
import site.addzero.network.call.music.model.MusicSearchRequest
import site.addzero.vibepocket.dto.SearchRequest

/**
 * 音乐搜索相关路由（网易云等）
 */
@Bean
fun Route.musicRoutes() {
    route("/api/music") {

        /**
         * 按关键词搜索歌曲（GET）
         *
         * Tag: music
         * Query: query [String] 搜索关键词
         * Responses:
         *   - 200 搜索结果
         *   - 400 缺少 query 参数
         */
        get("/search") {
            val query = call.request.queryParameters["query"]
            if (query.isNullOrBlank()) {
                throw IllegalArgumentException("Query parameter 'query' is required.")
            }
            val client = MusicSearchClient()
            val results = client.search(MusicSearchRequest(query))
            call.respond(results ?: emptyMap<String, Any>())
        }

        /**
         * 按关键词搜索歌曲（POST JSON）
         *
         * Tag: music
         * Body: application/json [SearchRequest]
         * Responses:
         *   - 200 搜索结果
         */
        post("/search") {
            val req = call.receive<SearchRequest>()
            val client = MusicSearchClient()
            val results = client.search(MusicSearchRequest(req.query))
            call.respond(results ?: emptyMap<String, Any>())
        }
    }
}
