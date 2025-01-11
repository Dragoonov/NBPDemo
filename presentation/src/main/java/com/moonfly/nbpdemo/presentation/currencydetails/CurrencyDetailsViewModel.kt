package com.moonfly.nbpdemo.presentation.currencydetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moonfly.nbpdemo.domain.model.Currency
import com.moonfly.nbpdemo.domain.model.DetailedCurrency
import com.moonfly.nbpdemo.domain.repository.Response
import com.moonfly.nbpdemo.domain.usecases.GetCurrencyDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.math.absoluteValue

@HiltViewModel
class CurrencyDetailsViewModel @Inject constructor(
    private val getCurrencyDetailsUseCase: GetCurrencyDetailsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val currencyCode = savedStateHandle.get<String>(CURRENCY_CODE_KEY) ?: ""

    private var _uiState = MutableStateFlow(CurrencyDetailsState())
    val uiState: StateFlow<CurrencyDetailsState> = _uiState.asStateFlow()

    init {
        reloadDetails()
    }

    fun onRefreshClicked() {
        reloadDetails()
    }

    private fun reloadDetails() {
        _uiState.update { it.copy(isLoading = true, isError = false) }
        viewModelScope.launch {
            val currencyResponse = getCurrencyDetailsUseCase(currencyCode)
            if (currencyResponse is Response.Error) {
                _uiState.update { it.copy(isError = true, isLoading = false) }
            } else {
                val currency = (currencyResponse as Response.Success).body
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isError = false,
                        detailedCurrency = generateUiData(currency)
                    )
                }
            }
        }
    }

    private fun generateUiData(detailedCurrency: DetailedCurrency): DetailedUICurrency {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val newestRate = detailedCurrency.historicalRates.lastOrNull()?.rate ?: .0
        return DetailedUICurrency(
            currency = detailedCurrency.currency,
            rates = detailedCurrency.historicalRates.map {
                DetailedRate(
                    rate = it.rate,
                    date = dateFormatter.format(it.date),
                    isDifferentAtLeastTenPercent = isRateDifferenceSignificant(newestRate, it.rate)
                )
            }.reversed()
        )
    }

    private fun isRateDifferenceSignificant(newestRate: Double, rate: Double): Boolean {
        val newestRateBigDecimal = BigDecimal.valueOf(newestRate)
        val rateBigDecimal = BigDecimal.valueOf(rate)
        val threshold = newestRateBigDecimal.multiply(BigDecimal("0.1"))
        return newestRateBigDecimal.subtract(rateBigDecimal).abs() > threshold
    }

    companion object {
        const val CURRENCY_CODE_KEY = "currencyCode"
    }

}

data class CurrencyDetailsState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val detailedCurrency: DetailedUICurrency = DetailedUICurrency(Currency("", ""), emptyList())
)

data class DetailedUICurrency(
    val currency: Currency,
    val rates: List<DetailedRate>
)

data class DetailedRate(
    val rate: Double,
    val date: String,
    val isDifferentAtLeastTenPercent: Boolean
)