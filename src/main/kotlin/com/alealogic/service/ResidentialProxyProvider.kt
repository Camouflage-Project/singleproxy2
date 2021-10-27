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
    fun getProxyByKey(key: String): ResidentialProxy? {
        val customerProxies = keyToProxies[key]
        return customerProxies
            ?.remove()
            ?.also { customerProxies.add(it) }
    }

    suspend fun addNewProxy(clientId: UUID) = addProxyToQueue(residentialProxyRepo.findById(clientId))

    @Synchronized
    private fun addProxyToQueue(residentialProxy: ResidentialProxy) {
        if (keyToProxies[residentialProxy.key] == null) keyToProxies[residentialProxy.key] = LinkedBlockingQueue()
        keyToProxies[residentialProxy.key]!!.add(residentialProxy)
    }
}
