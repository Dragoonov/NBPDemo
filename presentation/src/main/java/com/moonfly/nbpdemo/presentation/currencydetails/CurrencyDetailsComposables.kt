package com.moonfly.nbpdemo.presentation.currencydetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moonfly.nbpdemo.presentation.base.ErrorMessage
import com.moonfly.nbpdemo.presentation.base.LoadingBar
import com.moonfly.nbpdemo.presentation.base.theme.mediumSpace

@Composable
fun CurrencyDetailsMainView() {
    val viewModel: CurrencyDetailsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Box {
        when {
            !uiState.isError && !uiState.isLoading ->
                Column(
                    modifier = Modifier.padding(mediumSpace),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CurrencyHeader(
                        name = uiState.detailedCurrency.currency.name,
                        code = uiState.detailedCurrency.currency.code
                    )
                    HistoricalRates(rates = uiState.detailedCurrency.rates)
                }

            uiState.isError -> ErrorMessage(viewModel::onRefreshClicked)
            else -> LoadingBar()
        }

    }
}

@Composable
fun CurrencyHeader(name: String, code: String) {
    Text(
        text = name,
        fontWeight = FontWeight.Bold
    )
    Text(text = code)
}

@Composable
fun HistoricalRates(rates: List<DetailedRate>) {
    LazyColumn {
        items(rates) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    it.rate.toString(),
                    color = if (it.isDifferentAtLeastTenPercent) Color.Red else Color.Black
                )
                Text(it.date)
            }
        }
    }
}