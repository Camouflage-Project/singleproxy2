package com.alealogic.model

import kotlinx.serialization.Serializable

@Serializable
data class DownloadRequest(
    val password: String,
    val platform: String
)
