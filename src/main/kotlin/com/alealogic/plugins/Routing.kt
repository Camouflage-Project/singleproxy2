package com.alealogic.plugins

import com.alealogic.domain.Platform
import com.alealogic.model.HeartbeatRequest
import com.alealogic.model.ProxyDescriptorResponse
import com.alealogic.model.RegistrationRequest
import com.alealogic.service.ConfigProvider
import com.alealogic.service.FileProvider
import com.alealogic.service.ResidentialProxyService
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
import io.ktor.features.HttpsRedirect
import io.ktor.http.ContentDisposition
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.defaultResource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.request.receive
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.response.respondFile
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.ktor.ext.inject
import java.io.File
import java.util.UUID

fun Application.configureRouting() {
    install(AutoHeadResponse)
    install(HttpsRedirect)
    install(CORS) {
        header("key")
        method(HttpMethod.Options)
        method(HttpMethod.Get)
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
        val configProvider by inject<ConfigProvider>()
        val proxyService by inject<ResidentialProxyService>()

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
            log.info("installation script={}", installationScript)
            call.respondText { installationScript }
        }

        get("/proxies") {
            val key = call.request.headers["key"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val proxies = proxyService.getResidentialProxyResponseByKey(key)

            call.respond(proxies)
        }

        get("/download-latest") {
            val queryParameters = call.request.queryParameters
            val key = queryParameters["key"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val platform = queryParameters["platform"] ?: return@get call.respond(HttpStatusCode.BadRequest)

            val (fileName, file) = fileProvider.getReleaseNameAndFile(key, Platform.valueOf(platform))

            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, fileName)
                    .toString()
            )
            log.info("filename={}", fileName)
            call.respondFile(file)
        }

        get("/proxy-descriptor") {
            val key = call.request.headers["key"] ?: return@get call.respond(HttpStatusCode.Unauthorized)
            val proxy = proxyService.getProxyByKey(key) ?: return@get call.respond(HttpStatusCode.NotFound)
            call.respond(
                ProxyDescriptorResponse("localhost", proxy.port, proxy.id.toString())
            )
        }

        post("/register-desktop-client") {
            val registrationRequest = call.receive<RegistrationRequest>()
            proxyService.register(
                UUID.fromString(registrationRequest.clientId)
            )

            call.respond(HttpStatusCode.OK)
        }

        get("/script") {
            call.respondText { "" }
        }

        post("/latest-version") {
            call.respondText { configProvider.getReleaseName() }
        }

        post("/heartbeat") {
            val heartbeatRequest = call.receive<HeartbeatRequest>()
            proxyService.updateHeartbeat(
                UUID.fromString(heartbeatRequest.clientId),
                heartbeatRequest.ip
            )

            call.respond(HttpStatusCode.OK)
        }

        get("/download-sample-1mb") {
            val file = withContext(Dispatchers.IO) { File("samplefile/1MB.txt") }
            call.respondFile(file)
        }

        get("/download-sample-5mb") {
            val file = withContext(Dispatchers.IO) { File("samplefile/5MB.txt") }
            call.respondFile(file)
        }

        get("/download-sample-10mb") {
            val file = withContext(Dispatchers.IO) { File("samplefile/10MB.txt") }
            call.respondFile(file)
        }

        static("/") {
            resources("frontend")
            defaultResource("frontend/index.html")
        }
    }
}
