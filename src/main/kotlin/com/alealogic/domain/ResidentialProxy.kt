package com.alealogic.domain

import java.time.LocalDateTime
import java.util.UUID

data class ResidentialProxy(
    val id: UUID = UUID.randomUUID(),
    val key: String,
    val port: Int,
    val platform: Platform,
    val registered: Boolean = false,
    val ipAddress: String? = null,
    val lastHeartbeat: LocalDateTime? = null,
    val created: LocalDateTime = LocalDateTime.now()
)
