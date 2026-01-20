package com.zjarlin.vibe.ui.theme

import androidx.compose.ui.graphics.Color

object VibeColors {
    val Background = Color(0xFF0F0F13)
    val Surface = Color(0xFF1E1E26)
    val SurfaceGlass = Color(0x332A2A35) // Low opacity for glass
    
    val NeoCyan = Color(0xFF00F0FF)
    val NeoPurple = Color(0xFFBD00FF)
    val NeoMagenta = Color(0xFFFF0055)
    val NeoPink = Color(0xFFFF00AA)
    
    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xB3FFFFFF)
    val TextDisabled = Color(0x66FFFFFF)
}

val GradientPrimary = listOf(
    VibeColors.NeoPurple,
    VibeColors.NeoCyan
)

val GradientSecondary = listOf(
    VibeColors.NeoMagenta,
    VibeColors.NeoPink
)
