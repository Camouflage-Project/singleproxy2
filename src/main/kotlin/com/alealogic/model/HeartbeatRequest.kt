package com.alealogic.model

import kotlinx.serialization.Serializable

@Serializable
data class HeartbeatRequest(
    val clientId: String,
    val ip: String
)
