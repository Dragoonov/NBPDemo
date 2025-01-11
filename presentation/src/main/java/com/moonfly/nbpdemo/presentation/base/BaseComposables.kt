package com.moonfly.nbpdemo.presentation.base

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.moonfly.nbpdemo.presentation.R
import com.moonfly.nbpdemo.presentation.base.theme.DarkColorScheme
import com.moonfly.nbpdemo.presentation.base.theme.LightColorScheme
import com.moonfly.nbpdemo.presentation.base.theme.Typography
import com.moonfly.nbpdemo.presentation.currencieslist.CurrenciesListMainView
import com.moonfly.nbpdemo.presentation.currencydetails.CurrencyDetailsMainView
import com.moonfly.nbpdemo.presentation.currencydetails.CurrencyDetailsViewModel
import com.moonfly.nbpdemo.presentation.navigation.Screen


@Composable
fun LoadingBar() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .testTag(LoadingBarTag)
        )
    }
}

@Composable
fun ErrorMessage(onRefreshClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = stringResource(R.string.generic_error_message))
            Button(onClick = onRefreshClick) {
                Text(text = stringResource(R.string.refresh))
            }
        }
    }
}

@Composable
fun NBPDemoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun RootView() {
    val navController = rememberNavController()
    Scaffold { innerPadding ->
        NavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            navController = navController,
            startDestination = Screen.CurrenciesList.title,
        ) {
            composable(route = Screen.CurrenciesList.title) {
                CurrenciesListMainView {
                    navController.navigate("${Screen.CurrencyDetails.title}/$it")
                }
            }
            composable("${Screen.CurrencyDetails.title}/{${CurrencyDetailsViewModel.CURRENCY_CODE_KEY}}",
                arguments = listOf(
                    navArgument(CurrencyDetailsViewModel.CURRENCY_CODE_KEY) {
                        type = NavType.StringType
                    }
                )) {
                CurrencyDetailsMainView()
            }
        }
    }
}