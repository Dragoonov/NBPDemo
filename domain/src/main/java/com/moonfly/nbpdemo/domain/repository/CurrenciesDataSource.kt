package com.moonfly.nbpdemo.domain.repository

import com.moonfly.nbpdemo.domain.model.DetailedCurrency
import com.moonfly.nbpdemo.domain.model.LatestCurrencyRate

interface CurrenciesDataSource {
    suspend fun getLatestCurrenciesRates(): Response<List<LatestCurrencyRate>>
    suspend fun getCurrencyDetails(code: String): Response<DetailedCurrency>
}