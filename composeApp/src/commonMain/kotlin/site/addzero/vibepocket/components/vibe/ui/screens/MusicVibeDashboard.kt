package com.zjarlin.vibe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.zjarlin.vibe.ui.components.GlassySurface
import com.zjarlin.vibe.ui.components.NeonCard
import com.zjarlin.vibe.ui.components.Waveformizer
import com.zjarlin.vibe.ui.theme.GradientPrimary
import com.zjarlin.vibe.ui.theme.VibeColors

// Note: Coil is assumed for AsyncImage, if not available, replace with placeholder Box

@Composable
fun MusicVibeDashboard() {
    val playerController = remember { MusicPlayerController() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VibeColors.Background)
    ) {
        // 1. Ambient Background Orbs
        BackgroundOrbs()

        // 2. Main Content overlaid on top with Glass effect
        Row(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Left Sidebar
            Sidebar (
                modifier = Modifier.width(240.dp).fillMaxHeight(),
                onNavigate = { route -> /* TODO: Navigate to $route */ }
            )

            // Center Player
            MainPlayer(
                controller = playerController,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )

            // Right Playlist
            RightPanel(
                modifier = Modifier.width(320.dp).fillMaxHeight(),
                onTrackSelect = { index -> /* TODO: Play track $index */ }
            )
        }
    }
}

// ... BackgroundOrbs, Sidebar, NavSpace remain the same ...

@Composable
fun MainPlayer(
    controller: MusicPlayerController,
    modifier: Modifier = Modifier
) {
    val track = controller.currentTrack

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(24.dp)) {
        // Visualizer Area
        NeonCard(modifier = Modifier.fillMaxWidth().height(400.dp)) {
            Box {
                // Background visualizer linked to playing state
                Waveformizer(
                    isPlaying = controller.isPlaying,
                    modifier = Modifier.fillMaxSize().padding(48.dp)
                )

                // Song Info overlay
                Column(
                    modifier = Modifier.align(Alignment.BottomStart).padding(32.dp)
                ) {
                    Text(
                        text = track.title,
                        fontSize = 32.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${track.artist} • ${track.album}",
                        fontSize = 16.sp,
                        color = VibeColors.TextSecondary
                    )
                }
            }
        }

        // Controls
        GlassySurface(modifier = Modifier.fillMaxWidth().height(120.dp)) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Interactive Track Progress
                Column(modifier = Modifier.weight(1f).padding(end = 32.dp)) {
                    val duration = track.durationSeconds
                    val position = (duration * controller.currentProgress).toInt()

                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(formatTime(position), color = VibeColors.TextSecondary, fontSize = 12.sp)
                        Text(formatTime(duration), color = VibeColors.TextSecondary, fontSize = 12.sp)
                    }
                    Spacer(Modifier.height(8.dp))

                    // Simple Slider replacement using generic Slider for interactivity
                    Slider(
                        value = controller.currentProgress,
                        onValueChange = { controller.seekTo(it) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = VibeColors.NeoCyan,
                            activeTrackColor = VibeColors.NeoPurple,
                            inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                        )
                    )
                }

                // Playback Buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    IconButton(onClick = { controller.previousTrack() }) {
                        Icon(Icons.Default.SkipPrevious, "Previous", tint = Color.White)
                    }

                    // Play/Pause Button
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(GradientPrimary))
                            .clickable { controller.togglePlayPause() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (controller.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (controller.isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    IconButton(onClick = { controller.nextTrack() }) {
                        Icon(Icons.Default.SkipNext, "Next", tint = Color.White)
                    }
                }
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%d:%02d".format(m, s)
}

@Composable
fun RightPanel(
    modifier: Modifier = Modifier,
    onTrackSelect: (Int) -> Unit
) {
    GlassySurface(modifier) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("UPCOMING", color = VibeColors.TextDisabled, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(10) { index ->
                    TrackItem(index) { onTrackSelect(index) }
                }
            }
        }
    }
}

@Composable
fun TrackItem(index: Int, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier.size(48.dp).clip(MaterialTheme.shapes.medium).background(Color.DarkGray)
        )
        Column {
            Text("Track ${index + 1}", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text("Artist Name", color = VibeColors.TextSecondary, fontSize = 12.sp)
        }
    }
}
