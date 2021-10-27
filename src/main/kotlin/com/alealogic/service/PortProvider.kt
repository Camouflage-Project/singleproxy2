package com.alealogic.service

import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.Socket

class PortProvider(private val residentialProxyService: ResidentialProxyService) {

    private var portToAssign = 10060
    private var previouslyAssignedPorts = runBlocking { residentialProxyService.findAll() }
        .map { it.port }
        .toHashSet()

    fun findNextAvailablePort(): Int {
        while (true) {
            val port: Int = portToAssign++
            if (isAvailable(port)) return port
        }
    }

    private fun isAvailable(port: Int): Boolean {
        if (previouslyAssignedPorts.contains(port)) return false

        var s: Socket? = null
        return try {
            s = Socket("localhost", port)
            false
        } catch (e: IOException) {
            true
        } finally {
            if (s != null) {
                try {
                    s.close()
                } catch (e: IOException) {
                    logger.error(e.message, e)
                }
            }
        }
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(PortProvider::class.java)
    }
}
