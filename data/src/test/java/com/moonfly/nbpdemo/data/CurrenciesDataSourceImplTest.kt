package com.moonfly.nbpdemo.data

import com.moonfly.nbpdemo.data.dto.HistoricalCurrencyRateDTO
import com.moonfly.nbpdemo.data.dto.HistoricalCurrencyRatesDTO
import com.moonfly.nbpdemo.data.dto.RateDTO
import com.moonfly.nbpdemo.data.dto.RatesDTO
import com.moonfly.nbpdemo.domain.model.DetailedCurrency
import com.moonfly.nbpdemo.domain.model.LatestCurrencyRate
import com.moonfly.nbpdemo.domain.repository.Response
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test

class CurrenciesDataSourceImplTest {

    private val currenciesService: CurrenciesService = mockk()
    private val apiHelper: ApiHelper = mockk()
    private val currencyTableCache: CurrencyTableCache = mockk()

    private val dataSource =
        CurrenciesDataSourceImpl(currenciesService, apiHelper, currencyTableCache)

    @Test
    fun `should successfully get latest currencies rates from both calls`() = runTest {
        //Given
        coEvery { currenciesService.getFreshCurrencies(any()) } returns mockk()
        coEvery {
            apiHelper.executeCallWithErrorHandling(
                any<suspend () -> retrofit2.Response<List<RatesDTO>>>(),
                any<suspend (List<RatesDTO>) -> Response<List<LatestCurrencyRate>>>()
            )
        } returns Response.Success(listOf(mockk())) andThen Response.Success(
            listOf(
                mockk(),
                mockk()
            )
        )

        //When
        val result = dataSource.getLatestCurrenciesRates()

        //Then
        assertEquals(3, (result as? Response.Success)?.body?.size)
    }

    @Test
    fun `should save tables with successful call`() = runTest {
        //Given
        coEvery { currencyTableCache.saveCurrencyTable(any(), any()) } just Runs
        val rates = listOf(
            RatesDTO("A", "", "2025-01-10", listOf(RateDTO("PLN", "PLN", 1.0))),
        )
        coEvery {
            apiHelper.executeCallWithErrorHandling<List<RatesDTO>, List<LatestCurrencyRate>>(
                any(),
                any()
            )
        } coAnswers {
            val successMapper =
                secondArg<suspend (List<RatesDTO>) -> Response<List<LatestCurrencyRate>>>()
            successMapper(rates)
        }

        //When
        dataSource.getLatestCurrenciesRates()

        //Then
        coVerify { currencyTableCache.saveCurrencyTable("PLN", "A") }
    }

    @Test
    fun `should return error from failing first call`() = runTest {
        //Given
        val throwable: Throwable = mockk()
        coEvery { currenciesService.getFreshCurrencies(any()) } returns mockk()
        coEvery {
            apiHelper.executeCallWithErrorHandling(
                any<suspend () -> retrofit2.Response<List<RatesDTO>>>(),
                any<suspend (List<RatesDTO>) -> Response<List<LatestCurrencyRate>>>()
            )
        } returns Response.Error.UnknownError(throwable) andThen Response.Success(
            listOf(
                mockk(),
                mockk()
            )
        )

        //When
        val result = dataSource.getLatestCurrenciesRates()

        //Then
        assertEquals(throwable, (result as? Response.Error.UnknownError)?.throwable)
    }

    @Test
    fun `should return error from failing second call`() = runTest {
        //Given
        val throwable: Throwable = mockk()
        coEvery { currenciesService.getFreshCurrencies(any()) } returns mockk()
        coEvery {
            apiHelper.executeCallWithErrorHandling(
                any<suspend () -> retrofit2.Response<List<RatesDTO>>>(),
                any<suspend (List<RatesDTO>) -> Response<List<LatestCurrencyRate>>>()
            )
        } returns Response.Success(listOf(mockk())) andThen Response.Error.UnknownError(throwable)

        //When
        val result = dataSource.getLatestCurrenciesRates()

        //Then
        assertEquals(throwable, (result as? Response.Error.UnknownError)?.throwable)
    }

    @Test
    fun `getCurrencies - no data from API`() = runTest {
        //Given
        coEvery {
            apiHelper.executeCallWithErrorHandling<Response<List<RatesDTO>>, List<LatestCurrencyRate>>(
                any(),
                any()
            )
        } coAnswers {
            val successMapper =
                secondArg<suspend (List<RatesDTO>) -> Response<List<LatestCurrencyRate>>>()
            successMapper(emptyList())
        }

        //When
        val result = dataSource.getLatestCurrenciesRates()

        //Then
        assertEquals(
            "No data from API",
            (result as? Response.Error.UnknownError)?.throwable?.message
        )
    }

    @Test
    fun `should get currency details successfully`() = runTest {
        //Given
        val currencyCode = "PLN"
        val currency = HistoricalCurrencyRatesDTO(
            "Test", currencyCode, currencyCode, listOf(
                HistoricalCurrencyRateDTO("Test", "2025-01-10", 1.0)
            )
        )

        coEvery { currenciesService.getCurrencyDetails(any(), any(), any(), any()) } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns currency
        }
        coEvery { currencyTableCache.getTableForCurrency(currencyCode) } returns "Test"
        coEvery {
            apiHelper.executeCallWithErrorHandling<Response<HistoricalCurrencyRatesDTO>, DetailedCurrency>(
                any(), any()
            )
        } coAnswers {
            val result = firstArg<suspend () -> retrofit2.Response<HistoricalCurrencyRatesDTO>>()()
            val successMapper =
                secondArg<suspend (HistoricalCurrencyRatesDTO) -> Response<DetailedCurrency>>()
            successMapper(result.body()!!)
        }

        // Act
        val result = dataSource.getCurrencyDetails(currencyCode)

        // Assert
        assertEquals(currencyCode, (result as? Response.Success)?.body?.currency?.code)
        coVerify { currencyTableCache.getTableForCurrency(currencyCode) }
        coVerify {
            apiHelper.executeCallWithErrorHandling<Response<HistoricalCurrencyRatesDTO>, DetailedCurrency>(
                any(),
                any()
            )
        }
    }

}