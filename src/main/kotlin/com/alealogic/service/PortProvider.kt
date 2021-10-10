package com.alealogic.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.Socket

class PortProvider {

    private var portToAssign = 10060

    fun findNextAvailablePort(): Int {
        while (true) {
            val port: Int = portToAssign++
            if (isAvailable(port)) return port
        }
    }

    private fun isAvailable(port: Int): Boolean {
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
