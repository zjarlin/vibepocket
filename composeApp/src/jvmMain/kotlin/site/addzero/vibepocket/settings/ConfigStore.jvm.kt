package site.addzero.vibepocket.settings

import java.io.File

/**
 * Desktop JVM 平台配置文件路径：`~/.vibepocket/config.json`
 */
actual fun getPlatformConfigPath(): String {
    return System.getProperty("user.home") + File.separator + ".vibepocket" + File.separator + "config.json"
}

/**
 * JVM 平台文件读取：使用 java.io.File API。
 *
 * @return 文件内容字符串，文件不存在时返回 null
 */
actual fun platformReadFile(path: String): String? {
    val file = File(path)
    return if (file.exists()) file.readText(Charsets.UTF_8) else null
}

/**
 * JVM 平台文件写入：使用 java.io.File API，自动创建父目录。
 */
actual fun platformWriteFile(path: String, content: String) {
    val file = File(path)
    file.parentFile?.mkdirs()
    file.writeText(content, Charsets.UTF_8)
}
