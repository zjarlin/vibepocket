package com.shadcn.ui.kmp

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.awt.Toolkit // For desktop JVM

actual fun platform(): String = "Desktop" // or "JVM"

@Composable
actual fun getScreenWidth(): Dp {
    val density = LocalDensity.current.density
    return (Toolkit.getDefaultToolkit().screenSize.width / density).dp
}

@Composable
actual fun getScreenHeight(): Dp {
    val density = LocalDensity.current.density
    return (Toolkit.getDefaultToolkit().screenSize.height / density).dp
}