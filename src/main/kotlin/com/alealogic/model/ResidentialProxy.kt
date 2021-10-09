package com.alealogic.model

import java.time.LocalDateTime
import java.util.UUID

data class ResidentialProxy(
    val id: UUID,
    val password: String,
    val port: String,
    val created: LocalDateTime
)
