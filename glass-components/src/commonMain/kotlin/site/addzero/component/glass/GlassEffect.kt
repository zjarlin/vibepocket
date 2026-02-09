package site.addzero.component.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * GlassEffect — 水玻璃效果核心 Modifier 扩展
 *
 * 提供三种玻璃效果 Modifier：
 * - [glassEffect] — 基础玻璃（深色底层 + 半透明线性渐变 + 玻璃边框）
 * - [neonGlassEffect] — 霓虹发光玻璃（深色底层 + 径向发光渐变 + 霓虹色边框）
 * - [liquidGlassEffect] — 液态玻璃（深色底层 + 多层渐变 + 光折射风格边框高光）
 *
 * 所有效果在 Desktop JVM 上通过 [GlassTheme.DarkSurface] 深色底层 + 半透明渐变叠加实现，
 * 无需原生模糊支持，确保文字可读性。
 *
 * 设计对齐 Apple Liquid Glass 设计语言的视觉风格。
 */

// ── 基础玻璃效果 ────────────────────────────────────────────

/**
 * 基础玻璃效果修饰符。
 *
 * 在深色底层 ([GlassTheme.DarkSurface]) 上叠加半透明线性渐变，
 * 并添加玻璃风格的半透明边框，模拟毛玻璃质感。
 *
 * @param shape 裁剪形状，默认 16dp 圆角
 * @param backgroundColor 玻璃表面色，默认 [GlassTheme.GlassSurface]（10% 白色）
 * @param borderColor 边框色，默认 [GlassTheme.GlassBorder]（25% 白色）
 * @param borderWidth 边框宽度，默认 1dp
 */
fun Modifier.glassEffect(
    shape: Shape = RoundedCornerShape(16.dp),
    backgroundColor: Color = GlassTheme.GlassSurface,
    borderColor: Color = GlassTheme.GlassBorder,
    borderWidth: Dp = 1.dp,
): Modifier {
    return this
        .clip(shape)
        // 深色底层 — 保证 Desktop JVM 无原生模糊时文字可读
        .background(GlassTheme.DarkSurface)
        // 半透明渐变叠加 — 模拟玻璃表面
        .background(
            brush = Brush.linearGradient(
                colors = listOf(
                    backgroundColor.copy(alpha = 0.8f),
                    backgroundColor.copy(alpha = 0.4f),
                )
            )
        )
        // 玻璃边框 — 半透明渐变模拟光线折射
        .border(
            width = borderWidth,
            brush = Brush.linearGradient(
                colors = listOf(
                    borderColor.copy(alpha = 0.8f),
                    borderColor.copy(alpha = 0.2f),
                )
            ),
            shape = shape,
        )
}

// ── 霓虹发光玻璃效果 ────────────────────────────────────────

/**
 * 霓虹发光玻璃效果修饰符。
 *
 * 在深色底层上叠加径向发光渐变，并添加霓虹色发光边框，
 * 营造赛博朋克风格的发光玻璃质感。
 *
 * @param shape 裁剪形状，默认 16dp 圆角
 * @param glowColor 发光颜色，默认 [GlassTheme.NeonCyan]
 * @param intensity 发光强度 (0.0 ~ 1.0)，默认 0.6
 */
fun Modifier.neonGlassEffect(
    shape: Shape = RoundedCornerShape(16.dp),
    glowColor: Color = GlassTheme.NeonCyan,
    intensity: Float = 0.6f,
): Modifier {
    return this
        .clip(shape)
        // 深色底层
        .background(GlassTheme.DarkSurface)
        // 径向发光渐变 — 从中心向外扩散的霓虹光晕
        .background(
            brush = Brush.radialGradient(
                colors = listOf(
                    glowColor.copy(alpha = 0.15f * intensity),
                    glowColor.copy(alpha = 0.05f * intensity),
                )
            )
        )
        // 霓虹发光边框
        .border(
            width = 2.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    glowColor.copy(alpha = intensity),
                    glowColor.copy(alpha = 0.3f * intensity),
                )
            ),
            shape = shape,
        )
}

// ── 液态玻璃效果 ────────────────────────────────────────────

/**
 * 液态玻璃效果修饰符。
 *
 * 对齐 macOS 26 Liquid Glass 设计语言：在深色底层上叠加多层半透明渐变，
 * 并使用多色渐变边框模拟光线折射高光效果，营造流动的液态玻璃质感。
 *
 * 实现细节：
 * 1. 深色底层 ([GlassTheme.DarkSurface]) 保证可读性
 * 2. 多层线性渐变叠加 — 主色 → 副色 → 主色，模拟液态流动
 * 3. 内部微光层 ([drawBehind]) — 从左上角扩散的白色微光，模拟光线折射
 * 4. 多色渐变边框 — 白色 → 主色 → 副色 → 白色，模拟玻璃边缘的光折射高光
 *
 * @param shape 裁剪形状，默认 24dp 圆角（液态玻璃通常使用更大圆角）
 * @param primaryColor 主渐变色，默认 [GlassTheme.NeonPurple]
 * @param secondaryColor 副渐变色，默认 [GlassTheme.NeonCyan]
 */
fun Modifier.liquidGlassEffect(
    shape: Shape = RoundedCornerShape(24.dp),
    primaryColor: Color = GlassTheme.NeonPurple,
    secondaryColor: Color = GlassTheme.NeonCyan,
): Modifier {
    return this
        .clip(shape)
        // 深色底层
        .background(GlassTheme.DarkSurface)
        // 多层渐变叠加 — 模拟液态玻璃的流动色彩
        .background(
            brush = Brush.linearGradient(
                colors = listOf(
                    primaryColor.copy(alpha = 0.15f),
                    secondaryColor.copy(alpha = 0.08f),
                    primaryColor.copy(alpha = 0.05f),
                )
            )
        )
        // 内部微光层 — 模拟光线折射的微妙高光
        .drawBehind {
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.06f),
                        Color.Transparent,
                    ),
                    center = Offset(size.width * 0.3f, size.height * 0.2f),
                    radius = size.minDimension * 0.8f,
                )
            )
        }
        // 光折射边框 — 多色渐变模拟玻璃边缘的光线折射高光
        .border(
            width = 1.5.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.6f),
                    primaryColor.copy(alpha = 0.4f),
                    secondaryColor.copy(alpha = 0.3f),
                    Color.White.copy(alpha = 0.1f),
                )
            ),
            shape = shape,
        )
}
