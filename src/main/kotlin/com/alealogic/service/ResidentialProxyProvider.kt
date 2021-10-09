package com.alealogic.service

import com.alealogic.repository.ResidentialProxyRepo
import kotlinx.coroutines.runBlocking
import java.util.concurrent.LinkedBlockingQueue

class ResidentialProxyProvider(residentialProxyRepo: ResidentialProxyRepo ) {

    private val passwordToProxies = runBlocking { residentialProxyRepo.findAll() }
        .groupBy { it.password }
        .mapValues { LinkedBlockingQueue(it.value) }

    @Synchronized
    fun getProxyPortByPassword(password: String): String? {
        val customerProxies = passwordToProxies[password]
        return customerProxies
            ?.remove()
            ?.also { customerProxies.add(it) }
            ?.port
    }

}
