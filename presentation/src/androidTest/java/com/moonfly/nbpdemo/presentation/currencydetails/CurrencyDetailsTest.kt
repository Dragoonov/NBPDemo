package com.moonfly.nbpdemo.presentation.currencydetails

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import com.moonfly.nbpdemo.domain.model.Currency
import com.moonfly.nbpdemo.presentation.R
import com.moonfly.nbpdemo.presentation.base.LoadingBarTag
import com.moonfly.nbpdemo.presentation.base.NBPDemoTheme
import org.junit.Rule
import org.junit.Test

class CurrencyDetailsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayCurrencyData() {
        composeTestRule.setContent {
            NBPDemoTheme {
                CurrencyDetailsContent(
                    uiState = CurrencyDetailsState(
                        isLoading = false,
                        isError = false,
                        detailedCurrency = DetailedUICurrency(
                            Currency("Polski", "PLN"),
                            listOf(
                                DetailedRate(1.0, "2025-01-10", false),
                                DetailedRate(2.0, "2025-01-11", true),
                            )
                        )
                    ),
                ) {}
            }
        }

        composeTestRule.apply {
            onNodeWithText("Polski").assertIsDisplayed()
            onNodeWithText("PLN").assertIsDisplayed()
            onNodeWithText("1.0").assertIsDisplayed()
            onNodeWithText("2.0").assertIsDisplayed()
            onNodeWithText("2025-01-10").assertIsDisplayed()
            onNodeWithText("2025-01-11").assertIsDisplayed()
        }
    }

    @Test
    fun shouldDisplayError() {
        composeTestRule.setContent {
            NBPDemoTheme {
                CurrencyDetailsContent(
                    uiState = CurrencyDetailsState(
                        isLoading = false,
                        isError = true,
                        detailedCurrency = DetailedUICurrency(
                            Currency("", ""),
                            listOf()
                        )
                    ),
                ) {}
            }
        }

        composeTestRule.apply {
            val errorMessage = InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.generic_error_message)
            val refreshMessage = InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.refresh)
            onNodeWithText(errorMessage).assertIsDisplayed()
            onNodeWithText(refreshMessage).assertIsDisplayed()
        }
    }

    @Test
    fun shouldDisplayLoading() {
        composeTestRule.setContent {
            NBPDemoTheme {
                CurrencyDetailsContent(
                    uiState = CurrencyDetailsState(
                        isLoading = true,
                        isError = false,
                        detailedCurrency = DetailedUICurrency(
                            Currency("", ""),
                            listOf()
                        )
                    ),
                ) {}
            }
        }

        composeTestRule.apply {
            onNodeWithTag(LoadingBarTag).assertIsDisplayed()
        }
    }
}