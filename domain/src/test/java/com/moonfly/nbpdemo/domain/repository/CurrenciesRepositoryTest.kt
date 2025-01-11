package com.moonfly.nbpdemo.domain.repository

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class CurrenciesRepositoryTest {

    private val currenciesDataSource: CurrenciesDataSource = mockk()
    private val repository = CurrenciesRepository(currenciesDataSource)

    @Test
    fun `getLatestCurrenciesRates should call valid method on data source`() = runTest {
        //Given
        coEvery { currenciesDataSource.getLatestCurrenciesRates() } returns mockk()

        //When
        repository.getLatestCurrenciesRatesCurrencies()

        //Then
        coVerify { currenciesDataSource.getLatestCurrenciesRates() }
    }

    @Test
    fun `getCurrencyDetails should call valid method on data source`() = runTest {
        //Given
        coEvery { currenciesDataSource.getCurrencyDetails(any()) } returns mockk()

        //When
        repository.getCurrencyDetails("PLN")

        //Then
        coVerify { currenciesDataSource.getCurrencyDetails("PLN") }
    }
}