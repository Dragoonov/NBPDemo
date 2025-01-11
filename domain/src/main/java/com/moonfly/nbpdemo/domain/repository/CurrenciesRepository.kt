package com.moonfly.nbpdemo.domain.repository

class CurrenciesRepository(private val gamesDataSource: CurrenciesDataSource) {

    suspend fun getLatestCurrenciesRatesCurrencies() = gamesDataSource.getLatestCurrenciesRates()

    suspend fun getCurrencyDetails(code: String) = gamesDataSource.getCurrencyDetails(code)
}