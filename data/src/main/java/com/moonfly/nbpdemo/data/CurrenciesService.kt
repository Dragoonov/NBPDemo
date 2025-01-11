package com.moonfly.nbpdemo.data

import com.moonfly.nbpdemo.data.dto.HistoricalCurrencyRatesDTO
import com.moonfly.nbpdemo.data.dto.RatesDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CurrenciesService {

    @GET("/api/exchangerates/tables/{table}")
    suspend fun getFreshCurrencies(
        @Path("table") table: String,
    ): Response<List<RatesDTO>>

    @GET("/api/exchangerates/rates/{table}/{code}/{startDate}/{endDate}")
    suspend fun getCurrencyDetails(
        @Path("table") table: String,
        @Path("code") code: String,
        @Path("startDate") startDate: String,
        @Path("endDate") endDate: String
    ): Response<HistoricalCurrencyRatesDTO>
}