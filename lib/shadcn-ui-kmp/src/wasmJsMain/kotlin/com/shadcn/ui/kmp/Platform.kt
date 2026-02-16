package com.shadcn.ui.kmp

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.w3c.dom.Window // Import Window

actual fun platform(): String = "Web (Wasm JS)"

@Composable
actual fun getScreenWidth(): Dp {
    // Assuming window is available in Wasm JS environment
    return (org.w3c.dom.window!!.innerWidth / org.w3c.dom.window!!.devicePixelRatio).dp
}

@Composable
actual fun getScreenHeight(): Dp {
    // Assuming window is available in Wasm JS environment
    return (org.w3c.dom.window!!.innerHeight / org.w3c.dom.window!!.devicePixelRatio).dp
}