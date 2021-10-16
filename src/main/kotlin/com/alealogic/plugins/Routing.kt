package com.alealogic.plugins

import com.alealogic.model.Platform
import com.alealogic.service.ConfigProvider
import com.alealogic.service.FileProvider
import com.alealogic.service.ResidentialProxyProvider
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.auth.basic
import io.ktor.auth.form
import io.ktor.auth.principal
import io.ktor.features.AutoHeadResponse
import io.ktor.features.CORS
import io.ktor.http.ContentDisposition
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.response.respondBytes
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    install(AutoHeadResponse)
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header(HttpHeaders.ContentType)
        allowCredentials = true
        allowNonSimpleContentTypes = true
        anyHost()
    }

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
        val fileProvider by inject<FileProvider>()
        val proxyProvider by inject<ResidentialProxyProvider>()
        val configProvider by inject<ConfigProvider>()

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

        get("/install") {
            val queryParameters = call.request.queryParameters
            val key = queryParameters["key"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val platform = queryParameters["platform"] ?: return@get call.respond(HttpStatusCode.BadRequest)

            val installationScript = fileProvider.getInstallationScript(key, Platform.valueOf(platform))
            log.error("installation script=$installationScript")
            call.respondText { installationScript }
        }

        get("/download-latest") {
            val queryParameters = call.request.queryParameters
            val key = queryParameters["key"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val platform = queryParameters["platform"] ?: return@get call.respond(HttpStatusCode.BadRequest)

            val (fileName, bytes) = fileProvider.getReleaseNameAndFile(key, Platform.valueOf(platform))

            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, fileName)
                    .toString()
            )
            log.error("filename=$fileName")
            call.respondBytes { bytes }
        }

        get("/proxy-port") {
            val key = call.request.headers["key"] ?: return@get call.respond(HttpStatusCode.Unauthorized)
            val proxyPort = proxyProvider.getProxyPortByKey(key) ?: return@get call.respond(HttpStatusCode.NotFound)
            call.respondText { proxyPort.toString() }
        }

        post("/register-desktop-client") {
            call.respond(HttpStatusCode.OK)
        }

        post("/script") {
            call.respondText { "" }
        }

        post("/latest-version") {
            call.respondText { configProvider.getReleaseName() }
        }

        post("/heartbeat") {
            call.respond(HttpStatusCode.OK)
        }

        get("/test") {
            call.respond(configProvider.getBaseUrl())
        }

        // Static plugin. Try to access `/static/index.html`
        static("/static") {
            resources("static")
        }
    }
}
