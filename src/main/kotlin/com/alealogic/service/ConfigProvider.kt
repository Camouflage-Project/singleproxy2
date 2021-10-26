package com.alealogic.service

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

class ConfigProvider {

    private val config: Config = ConfigFactory.load()

    fun getBaseUrl(): String = config.getString("baseUrl")
    fun getReleaseName(): String = config.getString("releaseName")
    fun getNodeIp(): String = config.getString("node.ip")
    fun getNodeLimitedUsername(): String = config.getString("node.limitedUser.name")
    fun getNodeLimitedUserPassword(): String = config.getString("node.limitedUser.password")
    fun getDesktopClientDirectory(): String = config.getString("desktopClientDirectory")
}
