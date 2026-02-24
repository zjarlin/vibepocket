package site.addzero.vibepocket.di

import org.babyfish.jimmer.sql.dialect.Dialect
import org.babyfish.jimmer.sql.dialect.PostgresDialect
import org.babyfish.jimmer.sql.dialect.SQLiteDialect
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.newKSqlClient
import org.babyfish.jimmer.sql.runtime.ConnectionManager
import org.babyfish.jimmer.sql.runtime.DefaultDatabaseNamingStrategy
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import org.koin.mp.KoinPlatform.getKoin
import org.postgresql.ds.PGSimpleDataSource
import org.sqlite.SQLiteDataSource
import site.addzero.vibepocket.jimmer.interceptor.BaseEntityDraftInterceptor
import site.addzero.vibepocket.model.DatasourceConfig
import java.util.concurrent.ConcurrentHashMap
import javax.sql.DataSource

data class DatasourceProperties(
    val enabled: Boolean = false,
    val url: String = "",
    val driver: String = "",
    val username: String? = null,
    val password: String? = null
)

data class DatabaseConfig(
    val sqlite: DatasourceProperties = DatasourceProperties(),
    val postgres: DatasourceProperties = DatasourceProperties()
)

private fun loadDatabaseConfig(): DatabaseConfig {
    val configUrl = object {}.javaClass.getResource("/application.yml")
        ?: return DatabaseConfig()
    
    val yamlContent = configUrl.readText()
    
    val sqliteEnabled = yamlContent.contains("sqlite:") && 
                        yamlContent.substringAfter("sqlite:").substringBefore("postgres:").contains("enabled: true")
    val sqliteUrl = yamlContent.substringAfter("sqlite:").substringBefore("postgres:")
                        .substringAfter("url:").substringBefore("\n").trim().trim('"', '\'')
    
    val postgresEnabled = yamlContent.contains("postgres:") && 
                          yamlContent.substringAfter("postgres:").contains("enabled: true")
    val postgresUrl = yamlContent.substringAfter("postgres:")
                        .substringAfter("url:").substringBefore("\n").trim().trim('"', '\'')
    
    return DatabaseConfig(
        sqlite = DatasourceProperties(
            enabled = sqliteEnabled,
            url = sqliteUrl,
            driver = "org.sqlite.SQLiteDriver"
        ),
        postgres = DatasourceProperties(
            enabled = postgresEnabled,
            url = postgresUrl,
            driver = "org.postgresql.Driver"
        )
    )
}

private val dbConfig = loadDatabaseConfig()

/**
 * 默认 SQLite 客户端 (Bean id: 默认)
 */
val sqlClient: KSqlClient?
    get() = try {
        getKoin().get<KSqlClient>()
    } catch (e: Exception) {
        null
    }

/**
 * Postgres 客户端 (Bean id: "postgres")
 */
val postgresSqlClient: KSqlClient?
    get() = try {
        getKoin().get<KSqlClient>(org.koin.core.qualifier.named("postgres"))
    } catch (e: Exception) {
        null
    }

/**
 * 公共 KSqlClient 构建器
 */
private fun buildSqlClient(
    dataSource: DataSource,
    dialect: org.babyfish.jimmer.sql.dialect.Dialect,
    baseEntityDraftInterceptor: BaseEntityDraftInterceptor
): KSqlClient = newKSqlClient {
    setDialect(dialect)
    setConnectionManager(ConnectionManager.simpleConnectionManager(dataSource))
    addDraftInterceptor(baseEntityDraftInterceptor)
    setDatabaseNamingStrategy(DefaultDatabaseNamingStrategy.LOWER_CASE)
    setConnectionManager {
        dataSource.connection.use { con ->
            proceed(con)
        }
    }
}

/**
 * 数据库相关的 Koin Module
 */
@Module
@Configuration
class DatabaseModule {

    /**
     * 默认主数据源 (SQLite)
     */
    @Single
    fun dataSource(): DataSource? {
        if (!dbConfig.sqlite.enabled) return null
        return SQLiteDataSource().apply {
            this.url = dbConfig.sqlite.url
        }
    }

    /**
     * 默认 SQL 客户端 (SQLite Dialect)
     */
    @Single
    fun sqlClient(dataSource: DataSource?, baseEntityDraftInterceptor: BaseEntityDraftInterceptor): KSqlClient? {
        if (dataSource == null) return null
        return buildSqlClient(dataSource, SQLiteDialect(), baseEntityDraftInterceptor)
    }

    /**
     * Postgres 数据源 (Neon)
     */
    @Single
    @Named("postgres")
    fun postgresDataSource(): DataSource? {
        if (!dbConfig.postgres.enabled) return null
        return PGSimpleDataSource().apply {
            this.setURL(dbConfig.postgres.url)
        }
    }

    /**
     * Postgres SQL 客户端 (Postgres Dialect)
     */
    @Single
    @Named("postgres")
    fun postgresSqlClient(
        @Named("postgres") dataSource: DataSource?,
        baseEntityDraftInterceptor: BaseEntityDraftInterceptor
    ): KSqlClient? {
        if (dataSource == null) return null
        return buildSqlClient(dataSource, PostgresDialect(), baseEntityDraftInterceptor)
    }

}

/**
 * 动态数据源管理器
 */
@Single
class DataSourceManager(
    private val baseEntityDraftInterceptor: BaseEntityDraftInterceptor
) {
    private val cache = ConcurrentHashMap<String, KSqlClient>()

    fun getSqlClient(config: DatasourceConfig): KSqlClient {
        val key = "${config.dbType}:${config.url}"
        return cache.getOrPut(key) {
            val ds = when (config.dbType.uppercase()) {
                "SQLITE" -> SQLiteDataSource().apply { this.url = config.url }
                "POSTGRES" -> PGSimpleDataSource().apply { this.setURL(config.url) }
                else -> throw IllegalArgumentException("Unsupported db type: ${config.dbType}")
            }
            val dialect: Dialect = when (config.dbType.uppercase()) {
                "SQLITE" -> SQLiteDialect()
                "POSTGRES" -> PostgresDialect()
                else -> throw IllegalArgumentException("Unsupported db type: ${config.dbType}")
            }
            buildSqlClient(ds, dialect, baseEntityDraftInterceptor)
        }
    }
}

private fun loadSqlFile(filename: String): String {
    return object {}.javaClass.getResource("/sql/$filename")?.readText() ?: ""
}

/**
 * 初始化 SQLite 数据库表结构
 */
@Single(createdAtStart = true)
fun initDatabase(dataSource: DataSource?) {
    if (dataSource == null || !dbConfig.sqlite.enabled) return
    val sql = loadSqlFile("schema-sqlite.sql")
    dataSource.connection.use { conn ->
        conn.createStatement().use { stmt ->
            sql.split(";").filter { it.trim().isNotEmpty() }.forEach { stmt.executeUpdate(it.trim()) }
        }
    }
}

/**
 * 初始化 Postgres 数据库表结构
 */
@Single(createdAtStart = true)
fun initPostgresDatabase(@Named("postgres") dataSource: DataSource?) {
    if (dataSource == null || !dbConfig.postgres.enabled) return
    val sql = loadSqlFile("schema-postgres.sql")
    dataSource.connection.use { conn ->
        conn.createStatement().use { stmt ->
            sql.split(";").filter { it.trim().isNotEmpty() }.forEach { stmt.executeUpdate(it.trim()) }
        }
    }
}
