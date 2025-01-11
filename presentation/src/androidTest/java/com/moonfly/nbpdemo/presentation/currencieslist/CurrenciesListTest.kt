package com.moonfly.nbpdemo.presentation.currencieslist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import com.moonfly.nbpdemo.domain.model.Currency
import com.moonfly.nbpdemo.domain.model.LatestCurrencyRate
import com.moonfly.nbpdemo.domain.model.Rate
import com.moonfly.nbpdemo.presentation.R
import com.moonfly.nbpdemo.presentation.base.LoadingBarTag
import com.moonfly.nbpdemo.presentation.base.NBPDemoTheme
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class CurrenciesListTest {

    @get:Rule
    val composeTestRule = createComposeRule()


    @Test
    fun shouldDisplayListOfCurrencies() {
        composeTestRule.setContent {
            NBPDemoTheme {
                CurrencyListContent(
                    uiState = CurrenciesListState(
                        isLoading = false,
                        isError = false,
                        currencies = listOf(
                            LatestCurrencyRate(Currency("Polski", "PLN"), Rate(1.0, LocalDate.parse("2025-01-10"))),
                            LatestCurrencyRate(Currency("Euro", "EUR"), Rate(2.0, LocalDate.parse("2025-01-10"))),
                            LatestCurrencyRate(Currency("Dolar", "USD"), Rate(3.0, LocalDate.parse("2025-01-10"))),
                        )
                    ), {}, {}
                )
            }
        }

        composeTestRule.apply {
            onNodeWithText("Polski").assertIsDisplayed()
            onNodeWithText("Euro").assertIsDisplayed()
            onNodeWithText("Dolar").assertIsDisplayed()
            onNodeWithText("PLN").assertIsDisplayed()
            onNodeWithText("EUR").assertIsDisplayed()
            onNodeWithText("USD").assertIsDisplayed()
            onNodeWithText("1.0").assertIsDisplayed()
            onNodeWithText("2.0").assertIsDisplayed()
            onNodeWithText("3.0").assertIsDisplayed()
        }
    }

    @Test
    fun shouldDisplayError() {
        composeTestRule.setContent {
            NBPDemoTheme {
                CurrencyListContent(
                    uiState = CurrenciesListState(
                        isLoading = false,
                        isError = true,
                        currencies = listOf()
                    ), {}, {}
                )
            }
        }

        composeTestRule.apply {
            val errorMessage = InstrumentationRegistry.getInstrumentation().targetContext.getString(R.string.generic_error_message)
            val refreshMessage = InstrumentationRegistry.getInstrumentation().targetContext.getString(R.string.refresh)
            onNodeWithText(errorMessage).assertIsDisplayed()
            onNodeWithText(refreshMessage).assertIsDisplayed()
        }
    }

    @Test
    fun shouldDisplayLoading() {
        composeTestRule.setContent {
            NBPDemoTheme {
                CurrencyListContent(
                    uiState = CurrenciesListState(
                        isLoading = true,
                        isError = false,
                        currencies = listOf()
                    ), {}, {}
                )
            }
        }

        composeTestRule.apply {
            onNodeWithTag(LoadingBarTag).assertIsDisplayed()
        }
    }
}