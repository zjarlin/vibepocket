package site.addzero.vibepocket.api

/**
 * 内嵌 Server API 客户端（localhost:8080）
 *
 * 封装收藏、历史、Persona 的 CRUD 调用，使用 ktorfit 自动生成的实现。
 */
object ServerApiClient {

    private const val BASE_URL = "http://localhost:8080"

    // ktorfit 生成的实现，需要在实际使用时初始化
    private lateinit var apiService: ServerApi

}
