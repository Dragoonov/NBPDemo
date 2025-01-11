package com.moonfly.nbpdemo.presentation.currencieslist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moonfly.nbpdemo.presentation.base.ErrorMessage
import com.moonfly.nbpdemo.presentation.base.LoadingBar
import com.moonfly.nbpdemo.presentation.base.theme.mediumSpace

@Composable
fun CurrenciesListMainView(
    onNavigateToDetails: (code: String) -> Unit,
) {
    val viewModel: CurrenciesListViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigateToDetailsAction.collect {
            onNavigateToDetails(it)
        }
    }

    Box {
        when {
            !uiState.isError && !uiState.isLoading -> {
                LazyColumn {
                    items(uiState.currencies) {
                        ListItem(it.currency.name, it.currency.code, it.rate.rate) { code ->
                            viewModel.onCurrencyClicked(code)
                        }
                    }
                }
            }

            uiState.isError -> ErrorMessage(viewModel::onRefreshClicked)

            else -> LoadingBar()
        }
    }
}

@Composable
fun ListItem(
    title: String,
    code: String,
    rate: Double,
    onCurrencyClickListener: (code: String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(mediumSpace)
            .clickable {
                onCurrencyClickListener(code)
            }
    ) {
        Text(text = title, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Text(text = code, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.5f))
        Text(text = rate.toString(), fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.5f))
    }
}