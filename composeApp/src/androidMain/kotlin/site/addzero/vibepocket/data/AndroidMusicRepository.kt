package site.addzero.vibepocket.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import site.addzero.network.call.music.MusicSearchClient
import site.addzero.vibepocket.components.vibe.model.MusicRepository
import site.addzero.vibepocket.components.vibe.model.VibeSong

class AndroidMusicRepository : MusicRepository {
    private val client = MusicSearchClient()

    override suspend fun searchSongs(query: String): List<VibeSong> {
        return withContext(Dispatchers.IO) {
            try {
                val songs = client.searchSongs(query)
                songs.map { song ->
                    VibeSong(
                        id = song.id,
                        name = song.name,
                        artist = song.artists.joinToString(", ") { it.name },
                        album = song.album.name,
                        coverUrl = song.album.picUrl
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }
}
