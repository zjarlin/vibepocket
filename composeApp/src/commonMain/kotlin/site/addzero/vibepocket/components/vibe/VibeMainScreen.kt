package site.addzero.vibepocket.components.vibe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import site.addzero.vibepocket.components.vibe.model.MusicRepository
import site.addzero.vibepocket.components.vibe.model.FakeMusicRepository
import site.addzero.vibepocket.components.vibe.model.VibeSong

import site.addzero.vibepocket.player.VibePlayer

@Composable
fun VibeMainScreen(
    musicRepository: MusicRepository = FakeMusicRepository(),
    player: VibePlayer? = null
) {
    var selectedItem by remember { mutableStateOf(SidebarItem.Home) }

    // Use a custom darker theme for the Vibe UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VibeColors.Background)
    ) {
        // Background Gradient Glows (Optional, to add vibe)
        // ...

        Row(modifier = Modifier.fillMaxSize()) {
            Sidebar(
                selectedItem = selectedItem,
                onItemSelected = { selectedItem = it },
                modifier = Modifier.padding(end = 16.dp)
            )

            Box(modifier = Modifier.weight(1f).fillMaxHeight().padding(16.dp)) {
                when (selectedItem) {
                    SidebarItem.Home, SidebarItem.Library, SidebarItem.Playlists, SidebarItem.Explore -> {
                        MusicSection(musicRepository, player)
                    }
                    SidebarItem.Video -> {
                        ComingSoonPlaceholder("Video")
                    }
                    SidebarItem.Programming -> {
                        ComingSoonPlaceholder("Programming")
                    }
                    else -> {
                        ComingSoonPlaceholder(selectedItem.label)
                    }
                }
            }
        }
    }
}

@Composable
fun MusicSection(
    musicRepository: MusicRepository,
    player: VibePlayer? = null
) {
    var upcomingTracks by remember { mutableStateOf<List<VibeSong>>(emptyList()) }
    
    LaunchedEffect(Unit) {
        // Example query
        upcomingTracks = musicRepository.searchSongs("Neon") 
    }

    Row(modifier = Modifier.fillMaxSize()) {
        // Main Player Area
        Column(modifier = Modifier.weight(0.6f).fillMaxHeight()) {
           MusicPlayer(modifier = Modifier.weight(1f), player = player)
        }
        
        Spacer(modifier = Modifier.width(20.dp))
        
        // Secondary/Upcoming Area (Right Side)
        Column(modifier = Modifier.weight(0.4f).fillMaxHeight()) {
            GlassContainer(modifier = Modifier.fillMaxSize()) {
                Column {
                    Text("Upcoming Tracks", color = androidx.compose.ui.graphics.Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    upcomingTracks.take(10).forEach { song ->
                        Text(
                            text = "${song.name} - ${song.artist}",
                            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
