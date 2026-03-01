package site.addzero.vibepocket.jimmer.spi

import org.babyfish.jimmer.sql.dialect.Dialect
import org.babyfish.jimmer.sql.dialect.PostgresDialect
import org.koin.core.annotation.Single
import org.postgresql.ds.PGSimpleDataSource
import site.addzero.vibepocket.jimmer.di.DatasourceProperties
import javax.sql.DataSource

@Single
class PostgresDriver : DatabaseDriverSpi {

    override fun supports(driver: String): Boolean =
        driver.contains("postgres", ignoreCase = true)

    override fun createDataSource(props: DatasourceProperties): DataSource =
        PGSimpleDataSource().apply { setURL(props.url) }

    override fun dialect(): Dialect = PostgresDialect()

    override fun schemaFile(): String = "schema-postgres.sql"
}
