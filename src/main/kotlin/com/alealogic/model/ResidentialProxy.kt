package com.alealogic.model

import java.time.LocalDateTime
import java.util.UUID

data class ResidentialProxy(
    val id: UUID = UUID.randomUUID(),
    val key: String,
    val port: Int,
    val platform: Platform,
    val created: LocalDateTime = LocalDateTime.now()
)
