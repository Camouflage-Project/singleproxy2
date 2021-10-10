package com.alealogic

import com.alealogic.plugins.configureDI
import com.alealogic.plugins.configureMonitoring
import com.alealogic.plugins.configureRouting
import com.alealogic.plugins.configureSerialization
import com.alealogic.repository.shutDownRepo
import io.ktor.application.Application
import io.ktor.server.engine.addShutdownHook
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.stop
import io.ktor.server.netty.NettyApplicationEngine
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

fun main(args: Array<String>): Unit = start(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureRouting()
    configureMonitoring()
    configureSerialization()
    configureDI()
}

private fun start(args: Array<String>) {
    val applicationEnvironment = commandLineEnvironment(args)
    val engine = NettyApplicationEngine(applicationEnvironment)
    engine.addShutdownHook {
        runBlocking { shutDownRepo() }
        engine.stop(3, 5, TimeUnit.SECONDS)
    }
    engine.start(true)
}
