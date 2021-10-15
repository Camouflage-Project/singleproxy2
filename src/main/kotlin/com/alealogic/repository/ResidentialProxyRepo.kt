package com.alealogic.repository

import com.alealogic.model.Platform
import com.alealogic.model.ResidentialProxy
import com.github.jasync.sql.db.Connection
import com.github.jasync.sql.db.QueryResult
import com.github.jasync.sql.db.RowData
import com.github.jasync.sql.db.postgresql.PostgreSQLConnectionBuilder
import kotlinx.coroutines.future.await
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.TimeUnit

private val connectionPool = PostgreSQLConnectionBuilder.createConnectionPool {
    host = "localhost"
    port = 5432
    username = "postgres"
    password = "postgres"
    database = "postgres"
    maxActiveConnections = 100
    maxIdleTime = TimeUnit.MINUTES.toMillis(15)
    maxPendingQueries = 10_000
    connectionValidationInterval = TimeUnit.SECONDS.toMillis(30)
}.also { it.connect().get() }

suspend fun shutDownRepo(): Connection = connectionPool.disconnect().await()

class ResidentialProxyRepo {

    suspend fun save(proxy: ResidentialProxy): QueryResult =
        connectionPool
            .sendPreparedStatement(
                "insert into residential_proxy(id, key, port, platform, created) values (?, ?, ?, ?, ?)",
                listOf(proxy.id, proxy.key, proxy.port, proxy.platform, proxy.created)
            )
            .await()


    suspend fun findAll() =
        connectionPool
            .sendPreparedStatement("select id, key, port, platform, created from residential_proxy;")
            .await()
            .rows
            .map { it.toResidentialProxy() }

    private fun RowData.toResidentialProxy() =
        ResidentialProxy(
            this[0] as UUID,
            this[1] as String,
            this[2] as Int,
            Platform.valueOf(this[3] as String),
            this[4] as LocalDateTime
        )
}
