package com.alealogic.plugins

import com.alealogic.repository.ResidentialProxyRepo
import com.alealogic.service.ConfigProvider
import com.alealogic.service.FileProvider
import com.alealogic.service.PortProvider
import com.alealogic.service.ResidentialProxyProvider
import com.alealogic.service.ResidentialProxyService
import io.ktor.application.Application
import io.ktor.application.install
import org.koin.dsl.module
import org.koin.dsl.single
import org.koin.ktor.ext.Koin
import org.koin.logger.slf4jLogger

val appModule = module {
    single<ResidentialProxyRepo>()
    single<ResidentialProxyProvider>()
    single<PortProvider>()
    single<FileProvider>()
    single<ConfigProvider>()
    single<ResidentialProxyService>()
}

fun Application.configureDI() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}
