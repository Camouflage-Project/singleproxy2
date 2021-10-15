package com.alealogic.service

import com.alealogic.model.Platform
import com.alealogic.model.ResidentialProxy
import com.alealogic.repository.ResidentialProxyRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.UUID
import java.util.concurrent.TimeUnit

class FileProvider(
    private val portProvider: PortProvider,
    private val residentialProxyRepo: ResidentialProxyRepo
) {

    private val currentRelease = "ResidentialProxyClient_1_0_0"
    private val baseUrl = "http://localhost:8080"
    private val goCommand = "go"
    private val desktopClientDirectory = "/home/luka/go/src/desktopClient/cmd/desktopClient/"

    fun getInstallationScript(key: String, platform: Platform): String = currentRelease
        .let { "curl \"$baseUrl/download-latest?key=$key&platform=$platform\" -s -o $it && chmod +x $it && ./$it" }

    suspend fun getReleaseNameAndFile(key: String, platform: Platform): Pair<String, ByteArray> {
        val desktopClientKey = UUID.randomUUID().toString()
        val nextAvailablePort = portProvider.findNextAvailablePort()
        val ldflags =
            "-X desktopClient/config.Key=$desktopClientKey -X desktopClient/config.InjectedRemoteSshPort=$nextAvailablePort"

        residentialProxyRepo.save(
            ResidentialProxy(
                key = key,
                port = nextAvailablePort,
                platform = platform,
            )
        )

        val releaseName = buildDesktopClient(ldflags, platform)
        val byteArray =
            withContext(Dispatchers.IO) { Files.readAllBytes(Paths.get(desktopClientDirectory + releaseName)) }

        return releaseName to byteArray
    }

    private fun buildDesktopClient(ldflags: String, platform: Platform): String {
        val releaseName = currentRelease + if (platform == Platform.WINDOWS) ".exe" else ""
        val goArch = if (platform == Platform.MAC_OS_APPLE_SILICON) "arm64" else "amd64"
        val goos =
            when (platform) {
                Platform.LINUX -> "linux"
                Platform.WINDOWS -> "windows"
                Platform.MAC_OS_INTEL, Platform.MAC_OS_APPLE_SILICON -> "darwin"
            }

        try {
            val proc = ProcessBuilder(
                "env", "GOOS=$goos", "GOARCH=$goArch", goCommand, "build", "-o",
                releaseName, "-ldflags", ldflags
            )
                .directory(File(desktopClientDirectory))
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

            proc.waitFor(60, TimeUnit.MINUTES)
            proc.inputStream.bufferedReader().readText().also { logger.info(it) }
        } catch (e: IOException) {
            logger.error(e.message, e)
        }

        return releaseName
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(FileProvider::class.java)
    }
}
