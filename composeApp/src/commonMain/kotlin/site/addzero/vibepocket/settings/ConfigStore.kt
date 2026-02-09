package site.addzero.vibepocket.settings

import kotlinx.serialization.json.Json

/**
 * 获取平台特定的配置文件路径。
 *
 * - Desktop JVM: `~/.vibepocket/config.json`
 * - wasmJs: 使用 localStorage key 作为标识（实际存储在 localStorage 中）
 * - iOS: `~/Documents/config.json`（目标暂未启用）
 * - Android: `filesDir/config.json`（目标暂未启用）
 */
expect fun getPlatformConfigPath(): String

/**
 * 配置持久化存储层。
 *
 * 负责将用户输入的 API 配置安全地保存到本地，并在应用启动时加载。
 * 使用 kotlinx.serialization 进行 JSON 序列化/反序列化。
 *
 * 错误处理策略：
 * - 文件不存在：返回默认 [ModuleConfigs]
 * - JSON 格式损坏或不匹配：记录警告日志，返回默认 [ModuleConfigs]
 * - 写入失败：抛出 IOException，由调用方处理
 *
 * @property filePath 配置文件的完整路径
 */
class ConfigStore(private val filePath: String) {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
    }

    /**
     * 从 JSON 文件加载配置。
     *
     * 文件不存在或 JSON 无效时返回默认 [ModuleConfigs]，不会抛出异常。
     */
    fun load(): ModuleConfigs {
        return try {
            val content = platformReadFile(filePath)
            if (content.isNullOrBlank()) {
                ModuleConfigs()
            } else {
                json.decodeFromString<ModuleConfigs>(content)
            }
        } catch (e: Exception) {
            println("[ConfigStore] WARNING: Failed to load config from '$filePath': ${e.message}. Using defaults.")
            ModuleConfigs()
        }
    }

    /**
     * 将配置序列化为 JSON 并写入文件。
     *
     * 会自动创建父目录（如果不存在）。
     * 写入失败时抛出 IOException。
     */
    fun save(configs: ModuleConfigs) {
        val content = json.encodeToString<ModuleConfigs>(configs)
        platformWriteFile(filePath, content)
    }
}

/**
 * 平台特定的文件读取实现。
 *
 * @return 文件内容字符串，文件不存在时返回 null
 */
expect fun platformReadFile(path: String): String?

/**
 * 平台特定的文件写入实现。
 *
 * 会自动创建父目录（如果不存在）。
 */
expect fun platformWriteFile(path: String, content: String)
