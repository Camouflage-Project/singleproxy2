package com.alealogic.plugins

import com.alealogic.repository.ResidentialProxyRepo
import com.alealogic.service.ResidentialProxyProvider
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.application.call
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.auth.basic
import io.ktor.auth.form
import io.ktor.auth.principal
import io.ktor.features.AutoHeadResponse
import io.ktor.http.*
import io.ktor.http.content.static
import io.ktor.http.content.resources
import io.ktor.response.*
import io.ktor.routing.get
import io.ktor.routing.routing
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    install(AutoHeadResponse)

    authentication {
        basic(name = "myauth1") {
            realm = "Ktor Server"
            validate { credentials ->
                if (credentials.name == credentials.password) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }

        form(name = "myauth2") {
            userParamName = "user"
            passwordParamName = "password"
            challenge {
                /**/
            }
        }
    }

    routing {
        val repo by inject<ResidentialProxyRepo>()
        val proxyProvider by inject<ResidentialProxyProvider>()

        authenticate("myauth1") {
            get("/protected/route/basic") {
                val principal = call.principal<UserIdPrincipal>()!!
                call.respondText("Hello ${principal.name}")
            }
        }
        authenticate("myauth1") {
            get("/protected/route/form") {
                val principal = call.principal<UserIdPrincipal>()!!
                call.respondText("Hello ${principal.name}")
            }
        }

        get("/proxy-port") {
            val password = call.request.headers["password"] ?: return@get call.respond(HttpStatusCode.Unauthorized)
            val proxyPort = proxyProvider.getProxyPortByPassword(password) ?: return@get call.respond(HttpStatusCode.NotFound)
            call.respondText { proxyPort }
        }

        // Static plugin. Try to access `/static/index.html`
        static("/static") {
            resources("static")
        }
    }
}
