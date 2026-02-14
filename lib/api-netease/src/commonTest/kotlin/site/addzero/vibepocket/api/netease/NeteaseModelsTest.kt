//package site.addzero.vibepocket.api.netease
//
//import kotlinx.coroutines.test.runTest
//import kotlin.test.Test
//import kotlin.test.assertNotNull
//import kotlin.test.assertTrue
//
///**
// * NeteaseApi 集成测试（需要网络）
// *
// * 覆盖 MusicSearchClient 的全部业务方法。
// */
//@Suppress("NonAsciiCharacters")
//class NeteaseModelsTest {
//
//    private val client = MusicSearchClient
//
//    @Test
//    fun searchSongs() = runTest {
//        val songs = client.searchSongs("稻香", limit = 5)
//        assertTrue(songs.isNotEmpty(), "should return songs")
//        songs.forEach { println("  🎵 ${it.name} - ${it.artistNames}") }
//    }
//
//    @Test
//    fun searchArtists() = runTest {
//        val artists = client.searchArtists("周杰伦", limit = 5)
//        assertTrue(artists.isNotEmpty(), "should return artists")
//        artists.forEach { println("  🎤 ${it.name} (id=${it.id})") }
//    }
//
//    @Test
//    fun searchAlbums() = runTest {
//        val albums = client.searchAlbums("范特西", limit = 5)
//        assertTrue(albums.isNotEmpty(), "should return albums")
//        albums.forEach { println("  💿 ${it.name} (id=${it.id})") }
//    }
//
//    @Test
//    fun searchPlaylists() = runTest {
//        val playlists = client.searchPlaylists("华语经典", limit = 5)
//        assertTrue(playlists.isNotEmpty(), "should return playlists")
//        playlists.forEach { println("  📋 ${it.name} (tracks=${it.trackCount})") }
//    }
//
//    @Test
//    fun searchByLyric() = runTest {
//        val songs = client.searchByLyric("故事的小黄花")
//        assertTrue(songs.isNotEmpty(), "should find songs by lyric")
//        songs.forEach {
//            println("  🎵 ${it.name} - ${it.artistNames}")
//            println("    📝 ${it.matchedLyricText}")
//        }
//    }
//
//    @Test
//    fun getLyric() = runTest {
//        val songs = client.searchSongs("晴天", limit = 1)
//        val song = songs.firstOrNull()
//        assertNotNull(song, "should find '晴天'")
//
//        val lyric = client.getLyric(song.id)
//        assertNotNull(lyric.lrc?.lyric, "lyric should not be null")
//        println("  歌词预览:\n${lyric.lrc!!.lyric!!.take(200)}")
//    }
//
//    @Test
//    fun getSongDetail() = runTest {
//        val songs = client.searchSongs("七里香", limit = 1)
//        val song = songs.firstOrNull()
//        assertNotNull(song, "should find '七里香'")
//
//        val details = client.getSongDetail(listOf(song.id))
//        assertTrue(details.isNotEmpty(), "should return song details")
//        println("  详情: ${details.first().name} - ${details.first().artistNames}")
//    }
//
//    @Test
//    fun searchBySongAndArtist() = runTest {
//        val songs = client.searchBySongAndArtist("晴天", "周杰伦")
//        assertTrue(songs.isNotEmpty(), "should find songs by name+artist")
//        assertTrue(
//            songs.all { song -> song.artists.any { it.name.contains("周杰伦") } },
//            "all results should contain the artist"
//        )
//    }
//
//    @Test
//    fun getLyricBySongName() = runTest {
//        val lyric = client.getLyricBySongName("稻香", "周杰伦")
//        assertNotNull(lyric, "should find lyric by song name")
//        assertNotNull(lyric.lrc?.lyric, "lyric text should not be null")
//    }
//
//    @Test
//    fun getLyricsByFragment() = runTest {
//        val results = client.getLyricsByFragment("故事的小黄花", limit = 3)
//        assertTrue(results.isNotEmpty(), "should find songs+lyrics by fragment")
//        results.forEach {
//            println("  🎵 ${it.song.name} - ${it.song.artistNames}")
//            println("    📝 ${it.lyric.lrc?.lyric?.take(80)}...")
//        }
//    }
//}
