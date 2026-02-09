# 需求文档

## 简介

本特性对 Vibepocket 应用进行三大模块升级：
1. 将外部水玻璃（Glass）组件库替换为项目内子模块，修复 Desktop JVM 平台无原生模糊支持的问题，并对齐 macOS 26 液态玻璃设计语言
2. 将左侧导航菜单重构为树形元数据驱动结构，支持未来通过元编程或低代码 GUI 填充菜单数据
3. 新增设置页面，按模块分 Tab 管理各 AI 服务的 API Key 和 URL 配置

## 术语表

- **Glass_Component_Library**: 水玻璃风格 UI 组件库，作为 Gradle 子模块存在于项目中，提供 GlassCard、GlassButton、GlassTextField 等组件，可独立发布
- **Glass_Effect**: 水玻璃视觉效果的核心实现，通过深色底层 + 半透明渐变叠加模拟毛玻璃效果
- **Menu_Metadata**: 描述导航菜单项的数据结构，包含路由键、图标、父级路由键、别名、可见性等字段
- **Route_Key**: 菜单项的唯一标识符，使用页面 Composable 函数的全限定名（如 `site.addzero.vibepocket.music.MusicVibeScreen`）
- **Virtual_Parent**: 仅作为菜单分组目录存在的虚拟父节点，不对应实际的 Composable 页面
- **Menu_Tree**: 由 Menu_Metadata 节点组成的树形结构，驱动侧边栏菜单的渲染
- **Settings_Page**: 设置页面，按模块分 Tab 展示，用户可在其中配置各模块所需的 API Key 和 URL
- **API_Config**: 存储单个 API 服务连接信息的数据结构，包含 API Key/Token 和 Base URL
- **Config_Store**: 配置持久化存储层，负责将用户输入的 API 配置安全地保存到本地

## 需求

### 需求 1：水玻璃组件库子模块化

**用户故事：** 作为开发者，我希望将水玻璃组件库从外部依赖迁移为项目内 Gradle 子模块，以便独立维护、发布，并修复 Desktop JVM 平台的渲染问题。

#### 验收标准

1. THE Glass_Component_Library SHALL exist as a Gradle submodule within the project with its own `build.gradle.kts` using the `compose-lib` convention plugin
2. THE Glass_Component_Library SHALL provide the following composable components: GlassCard, NeonGlassCard, GlassButton, NeonGlassButton, GlassTextField, GlassTextArea, GlassSidebar, GlassStatCard, GlassInfoCard
3. WHEN rendering on Desktop JVM without native blur support, THE Glass_Effect SHALL apply a solid dark background layer (`DarkSurface`) beneath transparent gradient overlays to ensure text readability
4. THE Glass_Component_Library SHALL be publishable as an independent Maven artifact without depending on the main application module
5. WHEN the Glass_Component_Library submodule replaces the external dependency, THE composeApp module SHALL compile and render identically to the current behavior

### 需求 2：水玻璃视觉风格升级

**用户故事：** 作为用户，我希望应用的视觉风格对齐 macOS 26 液态玻璃设计语言，获得更现代、更有质感的界面体验。

#### 验收标准

1. THE Glass_Effect SHALL provide a `liquidGlassEffect` modifier that renders multi-layer translucent gradients with smooth light-refraction-style border highlights
2. THE Glass_Component_Library SHALL define a `GlassTheme` object containing a consistent color palette with dark backgrounds, surface colors, accent neon colors, and border/shadow tokens
3. WHEN a GlassCard is rendered, THE Glass_Component_Library SHALL apply rounded corners, a subtle inner glow gradient, and a semi-transparent border that simulates glass edge refraction
4. THE Glass_Component_Library SHALL support both dark-mode-only rendering with configurable accent color parameters for neon glow effects

### 需求 3：树形菜单元数据系统

**用户故事：** 作为开发者，我希望将侧边栏菜单抽象为树形元数据驱动结构，以便未来通过元编程（分析 @Route 注解）或低代码 GUI 动态填充菜单数据。

#### 验收标准

1. THE Menu_Metadata SHALL contain the following fields: routeKey (String, fully qualified composable function name), icon (nullable icon identifier), parentRouteKey (nullable String referencing parent node), menuNameAlias (display name), visible (Boolean), sortOrder (Int)
2. WHEN a Menu_Metadata node has a parentRouteKey that does not correspond to any registered composable page, THE Menu_Tree SHALL treat the parent as a Virtual_Parent directory grouping node
3. WHEN constructing the Menu_Tree from a flat list of Menu_Metadata, THE Menu_Tree builder SHALL produce a correctly nested tree structure where each node's children are sorted by sortOrder
4. WHEN a Menu_Metadata node has `visible` set to false, THE Menu_Tree renderer SHALL exclude that node and all its descendants from the sidebar display
5. THE Menu_Tree builder SHALL validate that no circular parent references exist in the metadata list
6. WHEN rendering the Menu_Tree, THE GlassSidebar SHALL display tree nodes with indentation levels corresponding to their depth in the tree hierarchy

### 需求 4：菜单元数据序列化

**用户故事：** 作为开发者，我希望菜单元数据支持 JSON 序列化和反序列化，以便未来从外部配置文件或元编程工具加载菜单结构。

#### 验收标准

1. THE Menu_Metadata SHALL be annotated with `@Serializable` and support JSON serialization via kotlinx.serialization
2. WHEN serializing a Menu_Metadata object to JSON and then deserializing the JSON back, THE result SHALL be equivalent to the original object
3. WHEN serializing a complete Menu_Tree (list of Menu_Metadata) to JSON and then deserializing, THE reconstructed tree structure SHALL be equivalent to the original tree

### 需求 5：设置页面 — 模块化 Tab 布局

**用户故事：** 作为用户，我希望在设置页面中按模块分 Tab 管理各 AI 服务的 API 配置，以便集中管理所有服务的连接信息。

#### 验收标准

1. WHEN a user navigates to the Settings_Page, THE Settings_Page SHALL display a tab bar with tabs for each registered module (音乐、编程、视频)
2. WHEN a user selects a module tab, THE Settings_Page SHALL display the API configuration fields specific to that module
3. THE Music tab SHALL display input fields for: Suno API Token, Suno API Base URL, Music Search API URL
4. THE Programming tab SHALL display a placeholder message indicating future AI coding model configuration
5. THE Video tab SHALL display a placeholder message indicating future video generation API configuration
6. WHEN a user modifies an API configuration field and triggers save, THE Config_Store SHALL persist the updated value to local storage

### 需求 6：API 配置数据模型与持久化

**用户故事：** 作为开发者，我希望 API 配置有清晰的数据模型和持久化机制，以便应用重启后配置不丢失。

#### 验收标准

1. THE API_Config SHALL contain the following fields: key (String, the API key or token), baseUrl (String, the API endpoint base URL), label (String, human-readable name for the config entry)
2. THE API_Config SHALL be annotated with `@Serializable` and support JSON serialization via kotlinx.serialization
3. WHEN serializing an API_Config object to JSON and then deserializing the JSON back, THE result SHALL be equivalent to the original object
4. WHEN the application starts, THE Config_Store SHALL load previously saved API configurations from local storage and populate the Settings_Page fields
5. IF the local storage file is corrupted or contains invalid JSON, THEN THE Config_Store SHALL log a warning and fall back to default empty configurations without crashing

### 需求 7：导航系统集成

**用户故事：** 作为用户，我希望点击侧边栏菜单项能正确导航到对应页面，包括新增的设置页面。

#### 验收标准

1. WHEN a user clicks a leaf node in the Menu_Tree sidebar, THE navigation system SHALL display the composable screen associated with that node's routeKey
2. WHEN a user clicks a Virtual_Parent node in the sidebar, THE Menu_Tree renderer SHALL toggle the expansion state of that node's children
3. WHEN the application starts, THE navigation system SHALL select the first visible leaf node in the Menu_Tree as the default active route
4. WHEN navigating to the settings route, THE navigation system SHALL display the Settings_Page with the previously selected tab preserved in memory
