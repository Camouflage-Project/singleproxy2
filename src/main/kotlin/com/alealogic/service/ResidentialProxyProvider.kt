package com.alealogic.service

import com.alealogic.domain.ResidentialProxy
import com.alealogic.repository.ResidentialProxyRepo
import kotlinx.coroutines.runBlocking
import java.util.UUID
import java.util.concurrent.LinkedBlockingQueue

class ResidentialProxyProvider(private val residentialProxyRepo: ResidentialProxyRepo) {

    private val keyToProxies = runBlocking { residentialProxyRepo.findAll() }
        .groupBy { it.key }
        .mapValues { LinkedBlockingQueue(it.value) }
        .toMutableMap()

    @Synchronized
    fun getProxyPortByKey(key: String): Int? {
        val customerProxies = keyToProxies[key]
        return customerProxies
            ?.remove()
            ?.also { customerProxies.add(it) }
            ?.port
    }

    suspend fun addNewProxy(clientId: UUID) = addProxyToQueue(residentialProxyRepo.findById(clientId))

    @Synchronized
    private fun addProxyToQueue(residentialProxy: ResidentialProxy) {
        val queue = keyToProxies[residentialProxy.key]
        if (queue == null) keyToProxies[residentialProxy.key] = LinkedBlockingQueue()
        queue?.add(residentialProxy)
    }
}
