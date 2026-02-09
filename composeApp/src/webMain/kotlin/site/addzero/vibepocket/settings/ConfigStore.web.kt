package site.addzero.vibepocket.settings

import kotlinx.browser.localStorage

/**
 * wasmJs 平台配置路径：使用 localStorage 的 key 名称。
 *
 * 浏览器环境没有文件系统，使用 localStorage 作为持久化后备方案。
 * 返回的 "路径" 实际上是 localStorage 中的 key。
 */
actual fun getPlatformConfigPath(): String {
    return "vibepocket_config"
}

/**
 * wasmJs 平台文件读取：从 localStorage 读取。
 *
 * @return localStorage 中对应 key 的值，不存在时返回 null
 */
actual fun platformReadFile(path: String): String? {
    return localStorage.getItem(path)
}

/**
 * wasmJs 平台文件写入：写入 localStorage。
 */
actual fun platformWriteFile(path: String, content: String) {
    localStorage.setItem(path, content)
}
