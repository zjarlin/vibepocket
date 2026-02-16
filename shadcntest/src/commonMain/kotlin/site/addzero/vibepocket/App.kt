package site.addzero.vibepocket

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.shadcn.ui.components.sidebar.LocalSidebarState
import com.shadcn.ui.components.sidebar.SidebarLayout
import com.shadcn.ui.components.sidebar.SidebarState
import com.shadcn.ui.components.sidebar.SidebarTrigger
import com.shadcn.ui.themes.Theme
import com.shadcn.ui.themes.styles
import dr.shadcn.kmp.themes.styles.ModernMinimalDark
import site.addzero.vibepocket.auth.WelcomePage
import site.addzero.vibepocket.music.ioc.generated.iocComposablesByTag
import site.addzero.vibepocket.navigation.*
import site.addzero.vibepocket.screens.PlaceholderScreen
import site.addzero.vibepocket.screens.WelcomeScreenWrapper


@Composable
@Preview
fun App() {
    Text("hello")
}
