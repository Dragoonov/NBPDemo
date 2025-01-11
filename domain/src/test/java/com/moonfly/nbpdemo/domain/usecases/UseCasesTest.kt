package com.moonfly.nbpdemo.domain.usecases

import com.moonfly.nbpdemo.domain.repository.CurrenciesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UseCasesTest {

    private val currenciesRepository: CurrenciesRepository = mockk()

    @Test
    fun `getLatestCurrenciesRates should call valid method on repository`() = runTest {
        //Given
        coEvery { currenciesRepository.getLatestCurrenciesRatesCurrencies() } returns mockk()

        //When
        val useCase = GetLatestCurrenciesRatesUseCase {
            currenciesRepository.getLatestCurrenciesRatesCurrencies()
        }
        useCase.invoke()

        //Then
        coVerify { currenciesRepository.getLatestCurrenciesRatesCurrencies() }
    }

    @Test
    fun `getCurrencyDetails should call valid method on repository`() = runTest {
        //Given
        coEvery { currenciesRepository.getCurrencyDetails(any()) } returns mockk()

        //When
        val useCase = GetCurrencyDetailsUseCase { code ->
            currenciesRepository.getCurrencyDetails(code)
        }
        useCase.invoke("PLN")

        //Then
        coVerify { currenciesRepository.getCurrencyDetails("PLN") }
    }

}