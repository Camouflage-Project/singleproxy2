package com.alealogic.plugins

import com.alealogic.repository.ResidentialProxyRepo
import com.alealogic.service.ResidentialProxyProvider
import io.ktor.application.install

import io.ktor.application.Application
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import org.koin.logger.slf4jLogger
import org.koin.dsl.single

val appModule = module {
    single<ResidentialProxyRepo>()
    single<ResidentialProxyProvider>()
}

fun Application.configureDI() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}
