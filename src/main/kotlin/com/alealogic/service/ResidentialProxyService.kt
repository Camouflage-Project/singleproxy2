package com.alealogic.service

import com.alealogic.domain.Platform
import com.alealogic.domain.ResidentialProxy
import com.alealogic.model.ResidentialProxiesResponse
import com.alealogic.model.ResidentialProxyDetails
import com.alealogic.repository.ResidentialProxyRepo
import kotlinx.datetime.toKotlinLocalDateTime
import java.util.UUID

class ResidentialProxyService(
    private val proxyRepo: ResidentialProxyRepo,
    private val proxyProvider: ResidentialProxyProvider
) {

    suspend fun create(id: UUID, key: String, port: Int, platform: Platform) =
        proxyRepo.save(
            ResidentialProxy(
                id = id,
                key = key,
                port = port,
                platform = platform,
            )
        )

    suspend fun getResidentialProxyResponseByKey(key: String) =
        proxyRepo.findAllByKey(key)
            .map { transform(it) }
            .let { ResidentialProxiesResponse(it) }

    suspend fun findAll() = proxyRepo.findAll()

    suspend fun register(clientId: UUID) = proxyRepo.register(clientId)
        .also { proxyProvider.addNewProxy(clientId) }

    suspend fun updateHeartbeat(clientId: UUID, ipAddress: String) =
        proxyRepo.updateHeartbeat(clientId, ipAddress)

    fun getProxyByKey(key: String) = proxyProvider.getProxyByKey(key)

    private fun transform(domain: ResidentialProxy): ResidentialProxyDetails =
        ResidentialProxyDetails(
            domain.platform.toString(),
            domain.registered,
            domain.ipAddress,
            domain.lastHeartbeat?.toKotlinLocalDateTime(),
            domain.created.toKotlinLocalDateTime()
        )
}
