package com.moonfly.nbpdemo.data

import com.moonfly.nbpdemo.data.dto.RateDTO
import com.moonfly.nbpdemo.domain.model.Currency
import com.moonfly.nbpdemo.domain.model.DetailedCurrency
import com.moonfly.nbpdemo.domain.model.LatestCurrencyRate
import com.moonfly.nbpdemo.domain.model.Rate
import com.moonfly.nbpdemo.domain.repository.CurrenciesDataSource
import com.moonfly.nbpdemo.domain.repository.Response
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CurrenciesDataSourceImpl(
    private val currenciesService: CurrenciesService,
    private val apiHelper: ApiHelper,
    private val currencyTableCache: CurrencyTableCache
) : CurrenciesDataSource {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override suspend fun getLatestCurrenciesRates(): Response<List<LatestCurrencyRate>> {
        val tableACurrencies = getCurrencies(TABLE_A)
        val tableBCurrencies = getCurrencies(TABLE_B)

        return when {
            tableACurrencies is Response.Success && tableBCurrencies is Response.Success -> {
                return Response.Success(tableACurrencies.body + tableBCurrencies.body)
            }
            tableACurrencies is Response.Error -> tableACurrencies
            else -> tableBCurrencies
        }
    }

    override suspend fun getCurrencyDetails(code: String): Response<DetailedCurrency> {
        return apiHelper.executeCallWithErrorHandling(
            {
                currenciesService.getCurrencyDetails(
                    table = currencyTableCache.getTableForCurrency(code),
                    code = code,
                    startDate = dateFormatter.format(LocalDate.now().minusWeeks(2)),
                    endDate = dateFormatter.format(LocalDate.now())
                )
            }
        ) { body ->
            Response.Success(
                DetailedCurrency(
                    currency = Currency(body.currency, body.code),
                    historicalRates = body.rates.map {
                        Rate(
                            it.rate,
                            parseDate(it.effectiveDate)
                        )
                    })
            )
        }
    }

    private suspend fun getCurrencies(table: String): Response<List<LatestCurrencyRate>> {
        return apiHelper.executeCallWithErrorHandling(
            { currenciesService.getFreshCurrencies(table) }
        ) { body ->
            val data = body.firstOrNull()
            if (data == null) {
                Response.Error.UnknownError(Throwable("No data from API"))
            } else {
                saveTablesToCache(data.tableType, data.rates)
                val rates = data.rates.map {
                    it.mapToLatestCurrencyRate(data.effectiveDate)
                }
                Response.Success(rates)
            }
        }
    }

    private suspend fun saveTablesToCache(table: String, rates: List<RateDTO>) {
        rates.forEach { currencyTableCache.saveCurrencyTable(it.code, table) }
    }

    private fun RateDTO.mapToLatestCurrencyRate(effectiveDate: String): LatestCurrencyRate {
        return LatestCurrencyRate(
            currency = Currency(name = currency, code = code),
            rate = Rate(rate = rate, date = parseDate(effectiveDate))
        )
    }

    private fun parseDate(date: String): LocalDate {
        return LocalDate.parse(date, dateFormatter)
    }

    private companion object {
        private const val TABLE_A = "A"
        private const val TABLE_B = "B"
    }

}