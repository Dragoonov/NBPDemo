package com.moonfly.nbpdemo.domain.model

import java.time.LocalDate

data class Rate(
    val rate: Double,
    val date: LocalDate
)