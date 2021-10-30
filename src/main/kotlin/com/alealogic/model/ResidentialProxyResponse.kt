package com.alealogic.model

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.serializers.LocalDateTimeComponentSerializer
import kotlinx.serialization.Serializable

@Serializable
data class ResidentialProxiesResponse(
    val proxies: List<ResidentialProxyDetails>
)

@Serializable
data class ResidentialProxyDetails(
    val platform: String,
    val registered: Boolean = false,
    val ipAddress: String? = null,
    @Serializable(with = LocalDateTimeComponentSerializer::class) val lastHeartbeat: LocalDateTime?,
    @Serializable(with = LocalDateTimeComponentSerializer::class) val created: LocalDateTime
)
