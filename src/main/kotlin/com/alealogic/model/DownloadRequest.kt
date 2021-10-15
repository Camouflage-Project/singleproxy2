package com.alealogic.model

import kotlinx.serialization.Serializable

@Serializable
data class DownloadRequest(
    val key: String,
    val platform: String
)
