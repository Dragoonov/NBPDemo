package com.moonfly.nbpdemo.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RatesDTO(
    @SerialName("table") val tableType: String,
    @SerialName("no") val tableNumber: String,
    @SerialName("effectiveDate") val effectiveDate: String,
    @SerialName("rates") val rates: List<RateDTO>
)