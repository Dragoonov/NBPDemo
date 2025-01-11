package com.moonfly.nbpdemo.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HistoricalCurrencyRateDTO(
    @SerialName("no") val tableNumber: String,
    @SerialName("effectiveDate") val effectiveDate: String,
    @SerialName("mid") val rate: Double
)