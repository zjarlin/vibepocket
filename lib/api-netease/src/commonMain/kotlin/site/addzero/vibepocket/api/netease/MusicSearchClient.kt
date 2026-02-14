//package site.addzero.vibepocket.api.netease
//
//import com.lt.lazy_people_http.config.LazyPeopleHttpConfig
//import io.ktor.client.plugins.*
//import io.ktor.http.*
//import site.addzero.core.network.apiClient
//
///**
// * 网易云音乐 API 客户端
// *
// * HTTP 层由 Ktorfit 声明式接口 [NeteaseApi] 处理，
// * 本 object 提供业务便捷方法（歌名+歌手搜索、歌词片段搜索等）。
// */
//object MusicSearchClient {
//
//    private const val BASE_URL = "https://music.163.com/api/"
//
//    var mytoken: String? = null
//
//    init {
//        apiClient.config {
//            defaultRequest {
//                url(BASE_URL)
//            }
//            defaultRequest {
//                headers {
//                    mytoken?.let {
//                        append(HttpHeaders.Authorization, it)
//                    }
//                }
//            }
//        }
//    }
//
//    private val config = LazyPeopleHttpConfig(apiClient)
////创建请求接口的实现类
//private val musicApi = NeteaseApi::class.createService(config)
//
//
//    /**
//     * 根据歌名和歌手搜索歌曲
//     *
//     * @param songName 歌名
//     * @param artistName 歌手名（可选，用于二次过滤）
//     */
//    suspend fun searchBySongAndArtist(songName: String, artistName: String? = null): List<NeteaseSearchSong> {
//        val keywords = if (artistName != null) "$songName $artistName" else songName
//        val songs = musicApi.searchSongs(keywords, limit = 10)
//        val result = songs.result
//        return if (artistName != null) {
//            val filter = result?.songs?.filter { song ->
//                song.artists.any { it.name.contains(artistName, ignoreCase = true) }
//            }
//            filter ?: emptyList()
//        } else {
//            result?.songs ?: emptyList()
//        }
//    }
//
//    /**
//     * 根据歌名获取歌词
//     *
//     * @param songName 歌名
//     * @param artistName 歌手名（可选，用于精确匹配）
//     * @return 歌词响应，找不到返回 null
//     */
//    suspend fun getLyricBySongName(songName: String, artistName: String? = null): NeteaseLyricResponse? {
//        val songs = searchBySongAndArtist(songName, artistName)
//        if (songs.isEmpty()) return null
//        return musicApi.getLyric(songs.first().id)
//    }
//
//    /**
//     * 根据歌词片段获取完整歌词
//     *
//     * @param lyricFragment 歌词片段
//     * @param limit 返回数量限制
//     * @param filterEmpty 是否过滤空歌词
//     * @return 歌曲与歌词组合列表
//     */
//    suspend fun getLyricsByFragment(
//        lyricFragment: String,
//        limit: Int = 5,
//        filterEmpty: Boolean = true,
//    ): List<SongWithLyric> {
//        val songs = musicApi.searchByLyric(lyricFragment, limit = limit).result?.songs
//        return songs?.mapNotNull { lyricSong ->
//            try {
//                val lyric = musicApi.getLyric(lyricSong.id)
//                if (filterEmpty && lyric.lrc?.lyric.isNullOrBlank()) {
//                    null
//                } else {
//                    val song = NeteaseSearchSong(
//                        id = lyricSong.id,
//                        name = lyricSong.name,
//                        artists = lyricSong.artists,
//                        album = lyricSong.album,
//                        duration = lyricSong.duration,
//                    )
//                    SongWithLyric(song, lyric)
//                }
//            } catch (_: Exception) {
//                null
//            }
//        }.orEmpty()
//    }
//}
