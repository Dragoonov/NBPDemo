package com.moonfly.nbpdemo.presentation.currencieslist

import com.moonfly.nbpdemo.domain.model.Currency
import com.moonfly.nbpdemo.domain.model.LatestCurrencyRate
import com.moonfly.nbpdemo.domain.model.Rate
import com.moonfly.nbpdemo.domain.repository.Response
import com.moonfly.nbpdemo.domain.usecases.GetLatestCurrenciesRatesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class CurrenciesListViewModelTest {

    private val getLatestCurrenciesRatesUseCase: GetLatestCurrenciesRatesUseCase = mockk()

    private lateinit var viewModel: CurrenciesListViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should reload currencies on initialization`() = runTest {
        //Given
        val rates = listOf(
            LatestCurrencyRate(Currency("PLN", "PLN"), Rate(1.0, LocalDate.parse("2025-01-10"))),
            LatestCurrencyRate(Currency("EUR", "EUR"), Rate(0.5, LocalDate.parse("2025-01-10")))
        )
        coEvery { getLatestCurrenciesRatesUseCase() } returns Response.Success(rates)

        //When
        viewModel = CurrenciesListViewModel(getLatestCurrenciesRatesUseCase)
        advanceUntilIdle()

        //Then
        val uiState = viewModel.uiState.first()
        assertFalse(uiState.isLoading)
        assertFalse(uiState.isError)
        assertEquals(rates, uiState.currencies)
        coVerify { getLatestCurrenciesRatesUseCase() }
    }

    @Test
    fun `should reload currencies on refresh`() = runTest {
        //Given
        val rates = listOf(
            LatestCurrencyRate(Currency("PLN", "PLN"), Rate(1.0, LocalDate.parse("2025-01-10"))),
            LatestCurrencyRate(Currency("EUR", "EUR"), Rate(0.5, LocalDate.parse("2025-01-10")))
        )
        coEvery { getLatestCurrenciesRatesUseCase() } returns Response.Success(rates)

        //When
        viewModel = CurrenciesListViewModel(getLatestCurrenciesRatesUseCase)
        viewModel.onRefreshClicked()
        advanceUntilIdle()

        //Then
        val uiState = viewModel.uiState.first()
        assertFalse(uiState.isLoading)
        assertFalse(uiState.isError)
        assertEquals(rates, uiState.currencies)
        coVerify(exactly = 2) { getLatestCurrenciesRatesUseCase() }
    }

    @Test
    fun `should generate failed state on failed reloading currencies`() = runTest {
        //Given
        coEvery { getLatestCurrenciesRatesUseCase.invoke() } returns Response.Error.UnknownError(Throwable("Error"))

        //When
        viewModel = CurrenciesListViewModel(getLatestCurrenciesRatesUseCase)
        advanceUntilIdle()

        //Then
        val uiState = viewModel.uiState.first()
        assertFalse(uiState.isLoading)
        assertTrue(uiState.isError)
        assertEquals(emptyList<LatestCurrencyRate>(), uiState.currencies)
    }

    @Test
    fun `should send navigate to details action on currency click`() = runTest {
        //Given
        coEvery { getLatestCurrenciesRatesUseCase.invoke() } returns Response.Success(emptyList())

        //When
        viewModel = CurrenciesListViewModel(getLatestCurrenciesRatesUseCase)
        viewModel.onCurrencyClicked("PLN")
        advanceUntilIdle()

        //Then
        assertEquals("PLN", viewModel.navigateToDetailsAction.first())
    }
}