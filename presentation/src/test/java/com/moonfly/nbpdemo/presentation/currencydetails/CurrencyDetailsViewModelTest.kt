package com.moonfly.nbpdemo.presentation.currencydetails

import androidx.lifecycle.SavedStateHandle
import com.moonfly.nbpdemo.domain.model.Currency
import com.moonfly.nbpdemo.domain.model.DetailedCurrency
import com.moonfly.nbpdemo.domain.model.Rate
import com.moonfly.nbpdemo.domain.repository.Response
import com.moonfly.nbpdemo.domain.usecases.GetCurrencyDetailsUseCase
import com.moonfly.nbpdemo.presentation.currencydetails.CurrencyDetailsViewModel.Companion.CURRENCY_CODE_KEY
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class CurrencyDetailsViewModelTest {

    private val getCurrencyDetailsUseCase: GetCurrencyDetailsUseCase = mockk()
    private val savedStateHandle: SavedStateHandle = mockk()

    private lateinit var viewModel: CurrencyDetailsViewModel

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
    fun `should get currency details on initialization`() = runTest {
        //Given
        val currencyCode = "PLN"
        val detailedCurrency = DetailedCurrency(
            Currency("PLN", "PLN"), listOf(
                Rate(1.0, LocalDate.parse("2025-01-10"))
            )
        )
        every { savedStateHandle.get<String>(CURRENCY_CODE_KEY) } returns currencyCode
        coEvery { getCurrencyDetailsUseCase(currencyCode) } returns Response.Success(
            detailedCurrency
        )

        //When
        viewModel = CurrencyDetailsViewModel(getCurrencyDetailsUseCase, savedStateHandle)
        advanceUntilIdle()

        //Then
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertFalse(uiState.isError)
        assertEquals(detailedCurrency.currency, uiState.detailedCurrency.currency)
        coVerify { getCurrencyDetailsUseCase(currencyCode) }
    }

    @Test
    fun `should get currency details on refresh`() = runTest {
        //Given
        val currencyCode = "PLN"
        val detailedCurrency = DetailedCurrency(
            Currency("PLN", "PLN"), listOf(
                Rate(1.0, LocalDate.parse("2025-01-10"))
            )
        )
        every { savedStateHandle.get<String>(CURRENCY_CODE_KEY) } returns currencyCode
        coEvery { getCurrencyDetailsUseCase(currencyCode) } returns Response.Success(
            detailedCurrency
        )

        //When
        viewModel = CurrencyDetailsViewModel(getCurrencyDetailsUseCase, savedStateHandle)
        viewModel.onRefreshClicked()
        advanceUntilIdle()

        //Then
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertFalse(uiState.isError)
        assertEquals(detailedCurrency.currency, uiState.detailedCurrency.currency)
        coVerify(exactly = 2) { getCurrencyDetailsUseCase(currencyCode) }
    }

    @Test
    fun `should generate failed ui state on failed getting currency details`() = runTest {
        //Given
        val currencyCode = "PLN"
        every { savedStateHandle.get<String>(CURRENCY_CODE_KEY) } returns currencyCode
        coEvery { getCurrencyDetailsUseCase(currencyCode) } returns Response.Error.UnknownError(
            Throwable("Error")
        )

        //When
        viewModel = CurrencyDetailsViewModel(getCurrencyDetailsUseCase, savedStateHandle)
        advanceUntilIdle()

        //Then
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertTrue(uiState.isError)
    }

    @Test
    fun `should mark one rate as different by at least 10 percent`() = runTest {
        //Given
        val currencyCode = "PLN"
        val detailedCurrency = DetailedCurrency(
            Currency("PLN", "PLN"), listOf(
                Rate(0.95, LocalDate.parse("2025-01-10")),
                Rate(2.0, LocalDate.parse("2025-01-10")),
                Rate(1.1, LocalDate.parse("2025-01-10")), // exactly 10%
                Rate(1.0, LocalDate.parse("2025-01-10")),
            )
        )
        every { savedStateHandle.get<String>(CURRENCY_CODE_KEY) } returns currencyCode
        coEvery { getCurrencyDetailsUseCase(currencyCode) } returns Response.Success(
            detailedCurrency
        )

        //When
        viewModel = CurrencyDetailsViewModel(getCurrencyDetailsUseCase, savedStateHandle)
        viewModel.onRefreshClicked()
        advanceUntilIdle()

        //Then
        val uiState = viewModel.uiState.value
        assertEquals(
            1,
            uiState.detailedCurrency.rates.filter { it.isDifferentAtLeastTenPercent }.size
        )
    }

    @Test
    fun `should return currencies sorted by date`() = runTest {
        //Given
        val currencyCode = "PLN"
        val detailedCurrency = DetailedCurrency(
            Currency("PLN", "PLN"), listOf(
                Rate(0.95, LocalDate.parse("2025-01-10")),
                Rate(2.0, LocalDate.parse("2025-01-11")),
                Rate(1.1, LocalDate.parse("2025-01-12")),
                Rate(1.0, LocalDate.parse("2025-01-13")),
            )
        )
        every { savedStateHandle.get<String>(CURRENCY_CODE_KEY) } returns currencyCode
        coEvery { getCurrencyDetailsUseCase(currencyCode) } returns Response.Success(
            detailedCurrency
        )

        //When
        viewModel = CurrencyDetailsViewModel(getCurrencyDetailsUseCase, savedStateHandle)
        viewModel.onRefreshClicked()
        advanceUntilIdle()

        //Then
        val uiState = viewModel.uiState.value
        assertEquals(
            uiState.detailedCurrency.rates,
            uiState.detailedCurrency.rates.sortedByDescending { LocalDate.parse(it.date) })
    }


}