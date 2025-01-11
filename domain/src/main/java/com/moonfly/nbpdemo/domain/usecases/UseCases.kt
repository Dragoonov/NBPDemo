package com.moonfly.nbpdemo.domain.usecases

import com.moonfly.nbpdemo.domain.model.DetailedCurrency
import com.moonfly.nbpdemo.domain.model.LatestCurrencyRate
import com.moonfly.nbpdemo.domain.repository.Response

fun interface GetLatestCurrenciesRatesUseCase : suspend () -> Response<List<LatestCurrencyRate>>
fun interface GetCurrencyDetailsUseCase : suspend (String) -> Response<DetailedCurrency>