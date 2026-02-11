# 实现计划: Vibepocket UI 大改造

## 概述

按模块递进实现：先搭建组件库子模块，再构建菜单元数据系统，最后实现设置页面和导航集成。每个阶段都包含对应的属性测试和单元测试，确保增量验证。

## 任务

- [x] 1. 创建 glass-components Gradle 子模块
  - [x] 1.1 创建 `glass-components/` 目录和 `build.gradle.kts`，使用 `site.addzero.conventions.compose-lib` convention plugin
    - 在 `settings.gradle.kts` 中 include `glass-components` 模块
    - 包名: `site.addzero.component.glass`
    - _Requirements: 1.1, 1.4_
  - [x] 1.2 实现 GlassTheme 颜色/Token 系统
    - 创建 `glass-components/src/commonMain/kotlin/site/addzero/component/glass/GlassTheme.kt`
    - 定义 DarkBackground, DarkSurface, GlassSurface, GlassBorder, NeonCyan, NeonPurple, NeonMagenta, NeonPink, 文字颜色等
    - 保持与现有 GlassColors 的颜色值兼容
    - _Requirements: 2.2_
  - [x] 1.3 实现 GlassEffect 核心 Modifier 扩展
    - 创建 `glass-components/src/commonMain/kotlin/site/addzero/component/glass/GlassEffect.kt`
    - 实现 `glassEffect()`, `neonGlassEffect()`, `liquidGlassEffect()` 三种 Modifier
    - Desktop JVM 使用 DarkSurface 底层 + 半透明渐变叠加
    - _Requirements: 1.3, 2.1, 2.3, 2.4_
  - [x] 1.4 实现所有 Glass 组件
    - GlassCard, NeonGlassCard, GlassButton, NeonGlassButton
    - GlassTextField, GlassTextArea
    - GlassSidebar (暂时保持现有 SidebarItem 接口，后续任务 3 改为 MenuNode)
    - GlassStatCard, GlassInfoCard
    - _Requirements: 1.2_
  - [x] 1.5 迁移 composeApp 依赖：移除外部 `compose-native-component-glass` 依赖，改为 `implementation(projects.glassComponents)`
    - 删除 `composeApp/src/commonMain/kotlin/site/addzero/component/glass/GlassEffect.kt`（本地覆盖文件）
    - 确保 composeApp 编译通过
    - _Requirements: 1.5_

- [x] 2. 检查点 — 确保组件库子模块编译通过
  - 确保所有代码编译通过，如有问题请询问用户。

- [x] 3. 实现菜单元数据系统
  - [x] 3.1 创建 MenuMetadata 和 MenuNode 数据模型
    - 创建 `composeApp/src/commonMain/kotlin/site/addzero/vibepocket/navigation/MenuMetadata.kt`
    - `MenuMetadata`: routeKey, menuNameAlias, icon, parentRouteKey, visible, sortOrder，标注 `@Serializable`
    - `MenuNode`: metadata, children, isVirtualParent
    - _Requirements: 3.1, 4.1_
  - [x] 3.2 实现 MenuTreeBuilder
    - 创建 `composeApp/src/commonMain/kotlin/site/addzero/vibepocket/navigation/MenuTreeBuilder.kt`
    - `buildTree(items: List<MenuMetadata>): List<MenuNode>` — 扁平列表构建树，子节点按 sortOrder 排序，检测循环引用
    - `flattenVisibleLeaves(roots: List<MenuNode>): List<MenuMetadata>` — 过滤不可见节点及其后代
    - 不在列表中的 parentRouteKey 对应节点标记为 isVirtualParent
    - _Requirements: 3.2, 3.3, 3.4, 3.5_
  - [x]* 3.3 编写 MenuMetadata 序列化往返属性测试
    - **Property 1: MenuMetadata JSON 序列化往返一致性**
    - 使用 Kotest property testing，随机生成 MenuMetadata，验证 `Json.decodeFromString(Json.encodeToString(metadata)) == metadata`
    - 最少 100 次迭代
    - **Validates: Requirements 4.2**
  - [x]* 3.4 编写 MenuTreeBuilder 属性测试
    - **Property 3: 树构建子节点按 sortOrder 排序**
    - **Property 4: 不可见节点从可见叶节点列表中排除**
    - **Property 5: 虚拟父节点检测**
    - 使用 Kotest property testing，随机生成 MenuMetadata 列表
    - 最少 100 次迭代
    - **Validates: Requirements 3.2, 3.3, 3.4, 7.3**
  - [x]* 3.5 编写 MenuTreeBuilder 单元测试
    - 测试空列表、单节点、循环引用抛异常、3+ 层深嵌套
    - _Requirements: 3.3, 3.4, 3.5_
  - [x] 3.6 创建默认菜单注册数据
    - 创建 `composeApp/src/commonMain/kotlin/site/addzero/vibepocket/navigation/DefaultMenuItems.kt`
    - 定义 `defaultMenuItems` 列表，包含音乐、编程、视频、设置四个菜单项
    - _Requirements: 3.1_

- [x] 4. 检查点 — 确保菜单元数据系统测试通过
  - 确保所有测试通过，如有问题请询问用户。

- [x] 5. 实现设置页面与配置持久化
  - [x] 5.1 创建 ApiConfig 和 ModuleConfigs 数据模型
    - 创建 `composeApp/src/commonMain/kotlin/site/addzero/vibepocket/settings/ApiConfig.kt`
    - `ApiConfig`: key, baseUrl, label，标注 `@Serializable`
    - `ModuleConfigs`: music, programming, video 三个 `List<ApiConfig>` 字段，带默认值
    - _Requirements: 6.1, 6.2_
  - [x] 5.2 实现 ConfigStore 配置持久化
    - 创建 `composeApp/src/commonMain/kotlin/site/addzero/vibepocket/settings/ConfigStore.kt`
    - `load(): ModuleConfigs` — 从 JSON 文件加载，文件不存在或 JSON 无效时返回默认值
    - `save(configs: ModuleConfigs)` — 序列化为 JSON 写入文件
    - 创建 `expect fun getPlatformConfigPath(): String` 和各平台 `actual` 实现
    - _Requirements: 5.6, 6.3, 6.4, 6.5_
  - [x]* 5.3 编写 ApiConfig 序列化往返属性测试
    - **Property 2: ApiConfig JSON 序列化往返一致性**
    - 使用 Kotest property testing，随机生成 ApiConfig
    - 最少 100 次迭代
    - **Validates: Requirements 6.3**
  - [ ]* 5.4 编写 ConfigStore 属性测试
    - **Property 6: 配置存储往返一致性**
    - **Property 7: 无效 JSON 回退到默认配置**
    - 使用 Kotest property testing，随机生成 ModuleConfigs 和无效 JSON 字符串
    - 最少 100 次迭代
    - **Validates: Requirements 5.6, 6.5**
  - [ ]* 5.5 编写 ConfigStore 单元测试
    - 测试文件不存在、空文件、部分字段缺失的 JSON
    - _Requirements: 6.4, 6.5_
  - [x] 5.6 实现 SettingsPage Composable
    - 创建 `composeApp/src/commonMain/kotlin/site/addzero/vibepocket/settings/SettingsPage.kt`
    - Tab 布局：音乐、编程、视频
    - 音乐 Tab: Suno API Token, Suno API Base URL, Music Search API URL 输入框 + 保存按钮
    - 编程/视频 Tab: 占位提示文字
    - 使用 Glass 组件库的 GlassCard, GlassTextField, NeonGlassButton 等
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5_

- [x] 6. 检查点 — 确保设置页面和配置持久化测试通过
  - 确保所有测试通过，如有问题请询问用户。

- [x] 7. 导航系统集成与收尾
  - [x] 7.1 升级 GlassSidebar 支持 MenuNode 树渲染
    - 修改 `glass-components` 中的 GlassSidebar，接受 `List<MenuNode>` 参数
    - 支持树形缩进显示、虚拟父节点展开/折叠
    - 保留向后兼容的 `List<SidebarItem>` 重载（可选）
    - _Requirements: 3.6, 7.2_
  - [x] 7.2 重构 App.kt 使用菜单元数据驱动导航
    - 用 `MenuTreeBuilder.buildTree(defaultMenuItems)` 构建菜单树
    - 用 `flattenVisibleLeaves()` 获取默认路由
    - 路由分发 when 分支使用 routeKey 全限定名
    - 接入 ConfigStore 和 SettingsPage
    - _Requirements: 7.1, 7.2, 7.3, 7.4_

- [x] 8. 最终检查点 — 确保全部编译通过和测试通过
  - 确保所有测试通过，如有问题请询问用户。

## 备注

- 标记 `*` 的任务为可选任务（属性测试和单元测试），可跳过以加快 MVP 进度
- 每个任务引用了具体的需求编号，确保可追溯性
- 检查点确保增量验证，避免问题累积
- 属性测试验证通用正确性属性，单元测试验证具体示例和边界情况
