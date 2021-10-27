package com.alealogic.model

import kotlinx.serialization.Serializable

@Serializable
data class ProxyDescriptorResponse(
    val host: String,
    val port: Int,
    val ipId: String
)
