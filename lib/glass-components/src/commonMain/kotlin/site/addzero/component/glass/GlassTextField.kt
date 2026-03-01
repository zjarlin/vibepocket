package site.addzero.component.glass

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import com.kyant.shapes.RoundedRectangle
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * GlassTextField — 单行玻璃风格输入框
 *
 * 使用 [glassEffect] 渲染半透明玻璃质感的单行文本输入框。
 * 内部使用 [BasicTextField] 实现，支持占位文字。
 *
 * @param value 当前输入值
 * @param onValueChange 输入变化回调
 * @param modifier 外部修饰符
 * @param placeholder 占位提示文字
 * @param enabled 是否启用，默认 true
 * @param singleLine 是否单行，默认 true
 * @param shape 输入框形状，默认 12dp 圆角
 */
@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    singleLine: Boolean = true,
    shape: Shape = RoundedRectangle(12.dp),
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        singleLine = singleLine,
        textStyle = TextStyle(
            color = GlassTheme.TextPrimary,
            fontSize = 14.sp,
        ),
        cursorBrush = SolidColor(GlassTheme.NeonCyan),
        modifier = modifier,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .glassEffect(shape = shape)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (value.isEmpty() && placeholder.isNotEmpty()) {
                    Text(
                        text = placeholder,
                        color = GlassTheme.TextTertiary,
                        fontSize = 14.sp,
                    )
                }
                innerTextField()
            }
        },
    )
}

/**
 * GlassTextArea — 多行玻璃风格文本域
 *
 * 使用 [glassEffect] 渲染半透明玻璃质感的多行文本输入区域。
 * 与 [GlassTextField] 的区别在于支持多行输入，且默认最小高度更大。
 *
 * @param value 当前输入值
 * @param onValueChange 输入变化回调
 * @param modifier 外部修饰符
 * @param placeholder 占位提示文字
 * @param enabled 是否启用，默认 true
 * @param shape 文本域形状，默认 16dp 圆角
 */
@Composable
fun GlassTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    shape: Shape = RoundedRectangle(16.dp),
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        singleLine = false,
        textStyle = TextStyle(
            color = GlassTheme.TextPrimary,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        ),
        cursorBrush = SolidColor(GlassTheme.NeonCyan),
        modifier = modifier,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .glassEffect(shape = shape)
                    .fillMaxWidth()
                    .heightIn(min = 120.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.TopStart,
            ) {
                if (value.isEmpty() && placeholder.isNotEmpty()) {
                    Text(
                        text = placeholder,
                        color = GlassTheme.TextTertiary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                    )
                }
                innerTextField()
            }
        },
    )
}

/**
 * GlassSearchField — 玻璃风格搜索框
 *
 * 带搜索图标的单行输入框，使用 [glassEffect] 渲染。
 * 支持回车触发搜索回调。
 *
 * @param value 当前输入值
 * @param onValueChange 输入变化回调
 * @param onSearch 搜索回调（回车触发）
 * @param modifier 外部修饰符
 * @param placeholder 占位提示文字
 * @param shape 搜索框形状，默认 24dp 圆角（胶囊形）
 */
@Composable
fun GlassSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "搜索...",
    shape: Shape = RoundedRectangle(24.dp),
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(
            color = GlassTheme.TextPrimary,
            fontSize = 14.sp,
        ),
        cursorBrush = SolidColor(GlassTheme.NeonCyan),
        modifier = modifier,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .glassEffect(shape = shape)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = "🔍 $placeholder",
                        color = GlassTheme.TextTertiary,
                        fontSize = 14.sp,
                    )
                }
                innerTextField()
            }
        },
    )
}
