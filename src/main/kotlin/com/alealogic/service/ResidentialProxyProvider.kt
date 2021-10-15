package com.alealogic.service

import com.alealogic.repository.ResidentialProxyRepo
import kotlinx.coroutines.runBlocking
import java.util.concurrent.LinkedBlockingQueue

class ResidentialProxyProvider(residentialProxyRepo: ResidentialProxyRepo) {

    private val keyToProxies = runBlocking { residentialProxyRepo.findAll() }
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
}
