package com.alealogic.model

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationRequest(
    val clientId: String
)
