package com.moonfly.nbpdemo.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HistoricalCurrencyRatesDTO(
    @SerialName("table") val tableType: String,
    @SerialName("currency") val currency: String,
    @SerialName("code") val code: String,
    @SerialName("rates") val rates: List<HistoricalCurrencyRateDTO>
)