package site.addzero.vibepocket.components.vibe.model

data class VibeSong(
    val id: Long,
    val name: String,
    val artist: String,
    val album: String,
    val coverUrl: String? = null
)

interface MusicRepository {
    suspend fun searchSongs(query: String): List<VibeSong>
}

// Dummy implementation for Preview / Default
class FakeMusicRepository : MusicRepository {
    override suspend fun searchSongs(query: String): List<VibeSong> {
        return listOf(
            VibeSong(1, "Midnight Drift", "Neon Dreams", "Ethereal Beats"),
            VibeSong(2, "Cyber City", "Synthwave Co", "Future Past"),
            VibeSong(3, "Glass Rain", "Lo-Fi Bot", "Rainy Days")
        )
    }
}
