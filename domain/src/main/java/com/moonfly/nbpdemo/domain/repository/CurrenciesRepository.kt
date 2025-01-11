package com.moonfly.nbpdemo.domain.repository

class CurrenciesRepository(private val currenciesDataSource: CurrenciesDataSource) {

    suspend fun getLatestCurrenciesRatesCurrencies() = currenciesDataSource.getLatestCurrenciesRates()

    suspend fun getCurrencyDetails(code: String) = currenciesDataSource.getCurrencyDetails(code)
}