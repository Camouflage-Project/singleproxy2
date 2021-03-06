package com.alealogic.service

import com.alealogic.domain.Platform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.util.UUID
import java.util.concurrent.TimeUnit

class FileProvider(
    private val portProvider: PortProvider,
    private val residentialProxyService: ResidentialProxyService,
    private val configProvider: ConfigProvider
) {

    private val baseUrl = configProvider.getBaseUrl()
    private val currentRelease = configProvider.getReleaseName()
    private val desktopClientDirectory = configProvider.getDesktopClientDirectory()
    private val goCommand = "go"

    fun getInstallationScript(key: String, platform: Platform): String = currentRelease
        .let { "curl \"$baseUrl/download-latest?key=$key&platform=$platform\" -o $it && chmod +x $it && ./$it" }

    suspend fun getReleaseNameAndFile(key: String, platform: Platform): Pair<String, File> {
        logger.info("key={}", key)
        val clientId = UUID.randomUUID()
        val nextAvailablePort = portProvider.findNextAvailablePort()

        residentialProxyService.create(clientId, key, nextAvailablePort, platform)

        val ldflags =
            "-s -w " +
                    "-X desktopClient/config.ClientId=$clientId " +
                    "-X desktopClient/config.InjectedRemoteSshPort=$nextAvailablePort " +
                    "-X desktopClient/config.BaseUrl=$baseUrl " +
                    "-X desktopClient/config.NodeIp=${configProvider.getNodeIp()} " +
                    "-X desktopClient/config.NodeLimitedUserName=${configProvider.getNodeLimitedUsername()} " +
                    "-X desktopClient/config.NodeLimitedUserPassword=${configProvider.getNodeLimitedUserPassword()}"

        val releaseName = buildDesktopClient(ldflags, platform)
        val file =
            withContext(Dispatchers.IO) { File(desktopClientDirectory + releaseName) }

        return releaseName to file
    }

    private fun buildDesktopClient(ldflags: String, platform: Platform): String {
        val releaseName = currentRelease + UUID.randomUUID() + if (platform == Platform.WINDOWS) ".exe" else ""
        val goArch = if (platform == Platform.MAC_OS_APPLE_SILICON) "arm64" else "amd64"
        val goos =
            when (platform) {
                Platform.LINUX -> "linux"
                Platform.WINDOWS -> "windows"
                Platform.MAC_OS_INTEL, Platform.MAC_OS_APPLE_SILICON -> "darwin"
            }

        logger.info("GOOS={}", goos)
        logger.info("GOARCH={}", goArch)
        logger.info("releaseName={}", releaseName)
        logger.info("ldflags={}", ldflags)

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
