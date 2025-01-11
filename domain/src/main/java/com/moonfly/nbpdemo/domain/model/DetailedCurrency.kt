package com.moonfly.nbpdemo.domain.model

data class DetailedCurrency(
    val currency: Currency,
    val historicalRates: List<Rate>
)