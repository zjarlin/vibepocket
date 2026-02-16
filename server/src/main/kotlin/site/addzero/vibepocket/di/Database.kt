package site.addzero.vibepocket.di

import org.babyfish.jimmer.sql.dialect.SQLiteDialect
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.newKSqlClient
import org.babyfish.jimmer.sql.runtime.ConnectionManager
import org.babyfish.jimmer.sql.runtime.DefaultDatabaseNamingStrategy
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.mp.KoinPlatform.getKoin
import org.sqlite.SQLiteDataSource
import site.addzero.vibepocket.jimmer.interceptor.BaseEntityDraftInterceptor
import javax.sql.DataSource

val sqlClient = getKoin().get<KSqlClient>()

/**
 * 数据库相关的 Koin Module
 *
 * Koin KCP 会扫描 @Module + @Single 注解自动生成 module 代码，
 * 不需要手写 module { single { ... } }
 */
@Module
@Configuration
class DatabaseModule {

    @Single
    fun dataSource(): DataSource = SQLiteDataSource().apply {
        this.url = "jdbc:sqlite:vibepocket.db"
    }




    @Single
    fun sqlClient(dataSource: DataSource, baseEntityDraftInterceptor: BaseEntityDraftInterceptor): KSqlClient =
        newKSqlClient {
            setDialect(SQLiteDialect())
            setConnectionManager(ConnectionManager.simpleConnectionManager(dataSource))
            addDraftInterceptor(baseEntityDraftInterceptor)
            setDatabaseNamingStrategy(DefaultDatabaseNamingStrategy.LOWER_CASE)
            setConnectionManager {
                dataSource.connection.use { con ->
                    proceed(con)
                }
            }
        }
}


/**
 * 初始化数据库表结构
 */
@Single(createdAtStart = true)
fun initDatabase(dataSource: DataSource) {
    dataSource.connection.use { conn ->
        conn.createStatement().use { stmt ->
            stmt.executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS music_task (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    task_id TEXT NOT NULL,
                    status TEXT NOT NULL DEFAULT 'queued',
                    title TEXT,
                    tags TEXT,
                    prompt TEXT,
                    mv TEXT,
                    audio_url TEXT,
                    video_url TEXT,
                    error_message TEXT,
                    created_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
                    updated_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime'))
                )
                """.trimIndent()
            )
            stmt.executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS app_config (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    key TEXT NOT NULL UNIQUE,
                    value TEXT NOT NULL,
                    description TEXT
                )
                """.trimIndent()
            )
            stmt.executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS datasource_config (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    owner TEXT NOT NULL,
                    name TEXT NOT NULL,
                    db_type TEXT NOT NULL DEFAULT 'SQLITE',
                    url TEXT NOT NULL,
                    username TEXT,
                    password TEXT,
                    driver_class TEXT,
                    enabled INTEGER NOT NULL DEFAULT 1,
                    description TEXT,
                    UNIQUE(owner, name)
                )
                """.trimIndent()
            )
            stmt.executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS favorite_track (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    track_id TEXT NOT NULL UNIQUE,
                    task_id TEXT NOT NULL,
                    audio_url TEXT,
                    title TEXT,
                    tags TEXT,
                    image_url TEXT,
                    duration REAL,
                    created_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime'))
                )
                """.trimIndent()
            )
            stmt.executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS music_history (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    task_id TEXT NOT NULL UNIQUE,
                    type TEXT NOT NULL DEFAULT 'generate',
                    status TEXT NOT NULL,
                    tracks_json TEXT NOT NULL DEFAULT '[]',
                    created_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime'))
                )
                """.trimIndent()
            )
            stmt.executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS persona_record (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    persona_id TEXT NOT NULL UNIQUE,
                    name TEXT NOT NULL,
                    description TEXT NOT NULL,
                    created_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime'))
                )
                """.trimIndent()
            )
        }
    }
}
