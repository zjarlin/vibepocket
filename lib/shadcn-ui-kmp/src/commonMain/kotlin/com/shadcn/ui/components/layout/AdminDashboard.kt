package com.shadcn.ui.components.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.shadcn.ui.components.sidebar.*
import com.shadcn.ui.themes.styles
import kotlinx.serialization.Serializable

@Serializable
data class TestRoute(val name: String) : NavKey

@Composable

fun AdminDashboard() {

    val backStack = remember { mutableStateListOf(TestRoute("Dashboard")) }



    SidebarProvider {

        val sidebarState = LocalSidebarState.current

        SidebarLayout(

            modifier = Modifier.fillMaxSize(),

            sidebarHeader = {
            SidebarHeader {
                Text(
                    text = "Shadcn Admin",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.styles.sidebarForeground,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }, sidebarContent = {
            SidebarContent {
                SidebarGroup {
                    SidebarLabel("Main")
                    SidebarGroupContent {
                        SidebarMenu {
                            SidebarMenuButton(
                                text = "Dashboard", onClick = {
                                    backStack.clear()
                                    backStack.add(TestRoute("Dashboard"))
                                }, isActive = backStack.lastOrNull()?.name == "Dashboard"
                            )
                            SidebarMenuButton(
                                text = "Components", onClick = {
                                    backStack.clear()
                                    backStack.add(TestRoute("Components"))
                                }, isActive = backStack.lastOrNull()?.name == "Components"
                            )
                            SidebarMenuButton(
                                text = "Settings", onClick = {
                                    backStack.clear()
                                    backStack.add(TestRoute("Settings"))
                                }, isActive = backStack.lastOrNull()?.name == "Settings"
                            )
                        }
                    }
                }
            }
        }, sidebarFooter = {
            SidebarFooter {
                Text(
                    text = "v1.0.0",
                    fontSize = 12.sp,
                    color = MaterialTheme.styles.sidebarForeground.copy(alpha = 0.5f)
                )
            }
        }) {
            Column(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.styles.background)
            ) {
                // Header / TopBar
                Row(
                    modifier = Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SidebarTrigger()
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = backStack.lastOrNull()?.name ?: "Unknown",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.styles.foreground
                    )
                }

                // Content Area with Nav3
                Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp)
                ) {
                    NavDisplay(
                        backStack = backStack,
                        onBack = { if (backStack.size > 1) backStack.removeLast() },
                        entryProvider = { route ->
                            NavEntry(route) {
                                when (route.name) {
                                    "Dashboard" -> DashboardContent()
                                    "Components" -> ComponentsContent()
                                    "Settings" -> SettingsContent()
                                    else -> Text("Unknown Route: ${route.name}")
                                }
                            }
                        })
                }
            }
        }
    }
}

@Composable
private fun DashboardContent() {
    Column {
        Text(
            text = "Welcome to the Dashboard",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.styles.foreground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "This is a test page for shadcn-ui-kmp components.",
            color = MaterialTheme.styles.foreground.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun ComponentsContent() {
    Column {
        Text(
            text = "Components Preview",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.styles.foreground
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Here we could add previews of various components
        Text(
            text = "List of components will be shown here.", color = MaterialTheme.styles.foreground.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun SettingsContent() {
    Column {
        Text(
            text = "Settings", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.styles.foreground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Configure your dashboard settings here.", color = MaterialTheme.styles.foreground.copy(alpha = 0.7f)
        )
    }
}
