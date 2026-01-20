package site.addzero.vibepocket.components.vibe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class SidebarItem(val label: String, val icon: ImageVector) {
    Home("Home", Icons.Default.Home),
    Library("Library", Icons.Default.LibraryMusic),
    Playlists("Playlists", Icons.Default.QueueMusic),
    Explore("Explore", Icons.Default.Explore),
    Video("Video", Icons.Default.Videocam),
    Programming("Programming", Icons.Default.Code),
    Settings("Settings", Icons.Default.Settings),
    Profile("Profile", Icons.Default.Person)
}

@Composable
fun Sidebar(
    selectedItem: SidebarItem,
    onItemSelected: (SidebarItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(250.dp)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // App Title
        Box(modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp)) {
           Column {
               NeonText(
                   text = "MUSIC VIBE",
                   style = androidx.compose.material3.MaterialTheme.typography.titleLarge.copy(
                       fontSize = 24.sp,
                       fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                       color = VibeColors.Primary
                   )
               )
               NeonText(
                   text = "CLIENT",
                   style = androidx.compose.material3.MaterialTheme.typography.bodyMedium.copy(
                       fontSize = 14.sp,
                       letterSpacing = 4.sp,
                       color = Color.White.copy(alpha = 0.7f)
                   )
               )
           }
        }

        // Navigation Items
        SidebarItem.entries.forEach { item ->
            val isSelected = item == selectedItem
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { onItemSelected(item) }
                    .background(
                        if (isSelected) Brush.horizontalGradient(
                            colors = listOf(
                                VibeColors.Primary.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        ) else androidx.compose.ui.graphics.SolidColor(Color.Transparent),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = if (isSelected) VibeColors.Secondary else Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = item.label,
                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                    style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
                )
                if (isSelected) {
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(VibeColors.Primary, androidx.compose.foundation.shape.CircleShape)
                    )
                }
            }
        }
    }
}
