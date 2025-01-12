package com.moonfly.nbpdemo.presentation.currencieslist

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moonfly.nbpdemo.domain.model.LatestCurrencyRate
import com.moonfly.nbpdemo.domain.repository.Response
import com.moonfly.nbpdemo.domain.usecases.GetLatestCurrenciesRatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrenciesListViewModel @Inject constructor(
    private val getLatestCurrenciesRatesUseCase: GetLatestCurrenciesRatesUseCase,
) : ViewModel() {

    private var _uiState = MutableStateFlow(CurrenciesListState())
    val uiState: StateFlow<CurrenciesListState> = _uiState.asStateFlow()

    private val _navigateToDetailsAction: Channel<String> = Channel()
    val navigateToDetailsAction = _navigateToDetailsAction.receiveAsFlow()

    init {
        reloadCurrencies()
    }

    fun onRefreshClicked() {
        reloadCurrencies()
    }

    fun onCurrencyClicked(code: String) {
        viewModelScope.launch {
            _navigateToDetailsAction.send(code)
        }
    }

    private fun reloadCurrencies() {
        _uiState.update { it.copy(isLoading = true, isError = false) }
        viewModelScope.launch {
            val currencies = getLatestCurrenciesRatesUseCase()
            if (currencies is Response.Error) {
                _uiState.update { it.copy(isError = true, isLoading = false) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isError = false,
                        currencies = (currencies as Response.Success).body
                    )
                }
            }
        }
    }
}

@Immutable
data class CurrenciesListState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val currencies: List<LatestCurrencyRate> = emptyList()
)