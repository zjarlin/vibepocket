package site.addzero.vibepocket.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import site.addzero.component.glass.*

/**
 * 设置页面的 Tab 定义。
 *
 * @property title Tab 显示名称
 * @property icon Tab 图标（emoji）
 */
private enum class SettingsTab(val title: String, val icon: String) {
    MUSIC("音乐", "🎵"),
    PROGRAMMING("编程", "💻"),
    VIDEO("视频", "🎬"),
}

/**
 * 设置页面 — 按模块分 Tab 管理各 AI 服务的 API 配置。
 *
 * 包含三个 Tab：音乐、编程、视频。
 * - 音乐 Tab：展示 Suno API Token、Suno API Base URL、Music Search API URL 的输入框和保存按钮
 * - 编程 Tab：占位提示，预留未来 AI 编程模型配置
 * - 视频 Tab：占位提示，预留未来视频生成 API 配置
 *
 * @param configStore 配置持久化存储实例，用于加载和保存配置
 */
@Composable
fun SettingsPage(configStore: ConfigStore) {
    // 从 ConfigStore 加载初始配置
    var moduleConfigs by remember {
        mutableStateOf(configStore.load())
    }
    var selectedTab by remember { mutableStateOf(SettingsTab.MUSIC) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GlassTheme.DarkBackground)
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 页面标题
            Text(
                text = "⚙️ 设置",
                color = GlassTheme.TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Tab 栏
            SettingsTabBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Tab 内容区
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    SettingsTab.MUSIC -> {
                        ApiConfigEditor(
                            configs = moduleConfigs.music,
                            onConfigChange = { index, updatedConfig ->
                                moduleConfigs = moduleConfigs.copy(
                                    music = moduleConfigs.music.toMutableList().apply {
                                        this[index] = updatedConfig
                                    }
                                )
                            },
                            onSave = { configStore.save(moduleConfigs) },
                        )
                    }

                    SettingsTab.PROGRAMMING -> {
                        PlaceholderTab(
                            icon = "💻",
                            title = "编程模块",
                            message = "AI 编程模型配置即将开放，敬请期待。",
                        )
                    }

                    SettingsTab.VIDEO -> {
                        PlaceholderTab(
                            icon = "🎬",
                            title = "视频模块",
                            message = "视频生成 API 配置即将开放，敬请期待。",
                        )
                    }
                }
            }
        }
    }
}

/**
 * 设置页面的 Tab 栏。
 *
 * 使用 GlassCard 作为容器，内部水平排列各 Tab 按钮。
 * 选中的 Tab 使用霓虹青色高亮显示。
 *
 * @param selectedTab 当前选中的 Tab
 * @param onTabSelected Tab 选中回调
 */
@Composable
private fun SettingsTabBar(
    selectedTab: SettingsTab,
    onTabSelected: (SettingsTab) -> Unit,
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            SettingsTab.entries.forEach { tab ->
                val isSelected = tab == selectedTab
                val interactionSource = remember { MutableInteractionSource() }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .then(
                            if (isSelected) {
                                Modifier.background(GlassTheme.NeonCyan.copy(alpha = 0.15f))
                            } else {
                                Modifier
                            }
                        )
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { onTabSelected(tab) },
                        )
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "${tab.icon} ${tab.title}",
                        color = if (isSelected) GlassTheme.NeonCyan else GlassTheme.TextSecondary,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    )
                }
            }
        }
    }
}

/**
 * API 配置编辑器 — 展示一组 API 配置输入框和保存按钮。
 *
 * 每个 [ApiConfig] 渲染为一个带标签的 [GlassTextField] 输入框。
 * 根据 [ApiConfig.label] 判断输入框类型：
 * - 包含 "Token" 的配置项：显示 key 字段的输入框
 * - 包含 "URL" 的配置项：显示 baseUrl 字段的输入框
 * - 其他：显示 key 字段的输入框
 *
 * @param configs 当前模块的 API 配置列表
 * @param onConfigChange 配置变更回调，参数为 (索引, 更新后的配置)
 * @param onSave 保存回调
 */
@Composable
fun ApiConfigEditor(
    configs: List<ApiConfig>,
    onConfigChange: (index: Int, ApiConfig) -> Unit,
    onSave: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        configs.forEachIndexed { index, config ->
            ApiConfigField(
                config = config,
                onConfigChange = { updatedConfig ->
                    onConfigChange(index, updatedConfig)
                },
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 保存按钮
        NeonGlassButton(
            text = "💾 保存配置",
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
            glowColor = GlassTheme.NeonCyan,
        )

        // 底部留白
        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * 单个 API 配置字段 — 根据 label 类型渲染对应的输入框。
 *
 * @param config 当前配置项
 * @param onConfigChange 配置变更回调
 */
@Composable
private fun ApiConfigField(
    config: ApiConfig,
    onConfigChange: (ApiConfig) -> Unit,
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // 标签
            Text(
                text = config.label,
                color = GlassTheme.TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )

            // 根据 label 判断编辑 key 还是 baseUrl
            val isUrlField = config.label.contains("URL", ignoreCase = true)

            GlassTextField(
                value = if (isUrlField) config.baseUrl else config.key,
                onValueChange = { newValue ->
                    onConfigChange(
                        if (isUrlField) {
                            config.copy(baseUrl = newValue)
                        } else {
                            config.copy(key = newValue)
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = if (isUrlField) "请输入 URL..." else "请输入 Token / Key...",
                shape = RoundedCornerShape(8.dp),
            )
        }
    }
}

/**
 * 占位 Tab 内容 — 用于编程和视频模块的占位提示。
 *
 * @param icon 模块图标（emoji）
 * @param title 模块标题
 * @param message 占位提示信息
 */
@Composable
private fun PlaceholderTab(
    icon: String,
    title: String,
    message: String,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        GlassCard(
            modifier = Modifier
                .widthIn(max = 400.dp)
                .padding(32.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = icon,
                    fontSize = 48.sp,
                )
                Text(
                    text = title,
                    color = GlassTheme.TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = message,
                    color = GlassTheme.TextTertiary,
                    fontSize = 14.sp,
                )
            }
        }
    }
}
