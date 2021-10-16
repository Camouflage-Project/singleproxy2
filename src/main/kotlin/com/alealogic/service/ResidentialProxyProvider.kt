package com.alealogic.service

import com.alealogic.domain.ResidentialProxy
import kotlinx.coroutines.runBlocking
import java.util.UUID
import java.util.concurrent.LinkedBlockingQueue

class ResidentialProxyProvider(private val residentialProxyService: ResidentialProxyService) {

    private val keyToProxies = runBlocking { residentialProxyService.findAll() }
        .groupBy { it.key }
        .mapValues { LinkedBlockingQueue(it.value) }

    @Synchronized
    fun getProxyPortByKey(key: String): Int? {
        val customerProxies = keyToProxies[key]
        return customerProxies
            ?.remove()
            ?.also { customerProxies.add(it) }
            ?.port
    }

    suspend fun addNewProxy(clientId: UUID) = addProxyToQueue(residentialProxyService.findById(clientId))

    @Synchronized
    private fun addProxyToQueue(residentialProxy: ResidentialProxy) =
        keyToProxies[residentialProxy.key]?.add(residentialProxy)

}
