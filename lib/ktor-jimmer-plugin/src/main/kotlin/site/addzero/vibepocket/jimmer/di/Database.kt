package site.addzero.vibepocket.jimmer.di

import com.typesafe.config.ConfigFactory
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.newKSqlClient
import org.babyfish.jimmer.sql.runtime.ConnectionManager
import org.babyfish.jimmer.sql.runtime.DefaultDatabaseNamingStrategy
import org.koin.core.qualifier.named
import org.koin.mp.KoinPlatform.getKoin
import site.addzero.vibepocket.jimmer.interceptor.BaseEntityDraftInterceptor
import site.addzero.vibepocket.jimmer.spi.DatabaseDriverSpi
import java.util.concurrent.ConcurrentHashMap
import javax.sql.DataSource

/**
 * 数据源属性（从 application.conf 的 datasources.xxx 读取）
 */
data class DatasourceProperties(
    val name: String = "",
    val enabled: Boolean = false,
    val url: String = "",
    val driver: String = "",
)

/**
 * 从 application.conf 动态加载所有 datasources 配置
 */
private fun loadAllDatasources(): List<DatasourceProperties> {
    val root = ConfigFactory.load()
    if (!root.hasPath("datasources")) return emptyList()
    val ds = root.getConfig("datasources")
    return ds.root().keys.map { name ->
        val section = ds.getConfig(name)
        DatasourceProperties(
            name = name,
            enabled = if (section.hasPath("enabled")) section.getBoolean("enabled") else false,
            url = if (section.hasPath("url")) section.getString("url") else "",
            driver = if (section.hasPath("driver")) section.getString("driver") else "",
        )
    }
}

private val allDatasources: List<DatasourceProperties> = loadAllDatasources()

/**
 * 从 Koin 中获取所有 DatabaseDriverSpi 实现，匹配给定 driver 字符串
 */
private fun resolveDriver(driver: String): DatabaseDriverSpi {
    val all = getKoin().getAll<DatabaseDriverSpi>()
    return all.firstOrNull { it.supports(driver) }
        ?: throw IllegalArgumentException(
            "No DatabaseDriverSpi found for driver '$driver'. " +
            "Available: ${all.map { it::class.simpleName }}"
        )
}

/**
 * 公共 KSqlClient 构建器
 */
private fun buildSqlClient(
    dataSource: DataSource,
    spi: DatabaseDriverSpi,
    interceptor: BaseEntityDraftInterceptor,
): KSqlClient = newKSqlClient {
    setDialect(spi.dialect())
    setConnectionManager(ConnectionManager.simpleConnectionManager(dataSource))
    addDraftInterceptor(interceptor)
    setDatabaseNamingStrategy(DefaultDatabaseNamingStrategy.LOWER_CASE)
    setConnectionManager { dataSource.connection.use { proceed(it) } }
}

private fun loadSqlFile(filename: String): String =
    object {}.javaClass.getResource("/sql/$filename")?.readText() ?: ""

/**
 * 执行 schema 初始化脚本
 */
private fun runSchema(dataSource: DataSource, schemaFile: String) {
    val sql = loadSqlFile(schemaFile)
    if (sql.isBlank()) return
    dataSource.connection.use { conn ->
        conn.createStatement().use { stmt ->
            sql.split(";")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .forEach { stmt.executeUpdate(it) }
        }
    }
}

/**
 * 动态多数据源注册器。
 *
 * 遍历 application.conf 中所有 datasources，通过 DatabaseDriverSpi 解析驱动类型，
 * 为每个 enabled 的数据源创建 DataSource + KSqlClient 并注册到 Koin。
 * 第一个 enabled 的作为默认（无 qualifier），所有数据源同时用 @Named("xxx") 注册。
 */
@org.koin.core.annotation.Single(createdAtStart = true)
class DatasourceRegistrar(
    private val interceptor: BaseEntityDraftInterceptor,
) {
    init {
        val koin = getKoin()
        var defaultRegistered = false

        for (props in allDatasources) {
            if (!props.enabled) continue

            val spi = resolveDriver(props.driver)
            val dataSource = spi.createDataSource(props)
            val sqlClient = buildSqlClient(dataSource, spi, interceptor)

            if (!defaultRegistered) {
                koin.declare<DataSource>(dataSource)
                koin.declare<KSqlClient>(sqlClient)
                defaultRegistered = true
            }
            koin.declare<DataSource>(dataSource, qualifier = named(props.name))
            koin.declare<KSqlClient>(sqlClient, qualifier = named(props.name))

            val schemaFile = spi.schemaFile()
            if (schemaFile != null) runSchema(dataSource, schemaFile)
        }
    }
}

/**
 * 对指定 DataSource 执行 SQLite schema 初始化（测试用）
 */
fun initDatabase(dataSource: DataSource) {
    runSchema(dataSource, "schema-sqlite.sql")
}

/**
 * 动态数据源管理器（运行时按 dbType + url 创建临时连接，同样走 SPI）
 */
@org.koin.core.annotation.Single
class DataSourceManager(
    private val interceptor: BaseEntityDraftInterceptor,
) {
    private val cache = ConcurrentHashMap<String, KSqlClient>()

    fun getSqlClient(dbType: String, url: String): KSqlClient {
        val key = "$dbType:$url"
        return cache.getOrPut(key) {
            val spi = resolveDriver(dbType)
            val props = DatasourceProperties(name = key, enabled = true, url = url, driver = dbType)
            val dataSource = spi.createDataSource(props)
            buildSqlClient(dataSource, spi, interceptor)
        }
    }
}
