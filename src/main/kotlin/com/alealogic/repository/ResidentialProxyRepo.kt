package com.alealogic.repository

import com.alealogic.domain.Platform
import com.alealogic.domain.ResidentialProxy
import com.github.jasync.sql.db.Connection
import com.github.jasync.sql.db.QueryResult
import com.github.jasync.sql.db.RowData
import com.github.jasync.sql.db.postgresql.PostgreSQLConnectionBuilder
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.future.await
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.TimeUnit

private val connectionPool = ConfigFactory.load().let {
    PostgreSQLConnectionBuilder.createConnectionPool {
        host = it.getString("db.host")
        port = it.getInt("db.port")
        database = it.getString("db.database")
        username = it.getString("db.username")
        password = it.getString("db.password")
        maxActiveConnections = 100
        maxIdleTime = TimeUnit.MINUTES.toMillis(15)
        maxPendingQueries = 10_000
        connectionValidationInterval = TimeUnit.SECONDS.toMillis(30)
    }
}.also { it.connect().get() }

suspend fun shutDownRepo(): Connection = connectionPool.disconnect().await()

class ResidentialProxyRepo {

    suspend fun save(proxy: ResidentialProxy): QueryResult =
        connectionPool
            .sendPreparedStatement(
                "INSERT INTO residential_proxy" +
                    "(id, key, port, platform, registered, ip_address, last_heartbeat, created) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                listOf(
                    proxy.id,
                    proxy.key,
                    proxy.port,
                    proxy.platform,
                    proxy.registered,
                    proxy.ipAddress,
                    proxy.lastHeartbeat, proxy.created
                )
            )
            .await()

    suspend fun findById(id: UUID): ResidentialProxy =
        connectionPool
            .sendPreparedStatement("SELECT * FROM residential_proxy where id = ?", listOf(id))
            .await()
            .rows
            .map { it.toResidentialProxy() }
            .first()

    suspend fun register(id: UUID): QueryResult =
        connectionPool
            .sendPreparedStatement("UPDATE residential_proxy SET registered = true WHERE id = ?", listOf(id))
            .await()

    suspend fun updateHeartbeat(id: UUID, ipAddress: String): QueryResult =
        connectionPool
            .sendPreparedStatement(
                "UPDATE residential_proxy SET ip_address = ?, last_heartbeat = ? WHERE id = ?",
                listOf(ipAddress, LocalDateTime.now(), id)
            )
            .await()

    suspend fun findAll() =
        connectionPool
            .sendPreparedStatement("SELECT * FROM residential_proxy")
            .await()
            .rows
            .map { it.toResidentialProxy() }

    suspend fun findAllWithLastHeartbeatWithin30Sec() =
        connectionPool
            .sendPreparedStatement("select * from residential_proxy where last_heartbeat > (current_timestamp - interval '30 seconds')")
            .await()
            .rows
            .map { it.toResidentialProxy() }

    suspend fun findAllByKey(key: String) =
        connectionPool
            .sendPreparedStatement("SELECT * FROM residential_proxy WHERE key = ? order by created", listOf(key))
            .await()
            .rows
            .map { it.toResidentialProxy() }

    private fun RowData.toResidentialProxy() =
        ResidentialProxy(
            this[0] as UUID,
            this[1] as String,
            this[2] as Int,
            Platform.valueOf(this[3] as String),
            this[4] as Boolean,
            this[5] as String?,
            this[6] as LocalDateTime?,
            this[7] as LocalDateTime
        )
}
