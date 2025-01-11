package com.moonfly.nbpdemo.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RateDTO(
    @SerialName("currency") val currency: String,
    @SerialName("code") val code: String,
    @SerialName("mid") val rate: Double,
)