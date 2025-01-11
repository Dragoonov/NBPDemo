package com.moonfly.nbpdemo.di

import com.moonfly.nbpdemo.domain.repository.CurrenciesDataSource
import com.moonfly.nbpdemo.domain.repository.CurrenciesRepository
import com.moonfly.nbpdemo.domain.usecases.GetCurrencyDetailsUseCase
import com.moonfly.nbpdemo.domain.usecases.GetLatestCurrenciesRatesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    fun provideCurrenciesRepository(dataSource: CurrenciesDataSource): CurrenciesRepository {
        return CurrenciesRepository(dataSource)
    }

    @Provides
    fun provideGetFreshCurrenciesUseCase(repository: CurrenciesRepository): GetLatestCurrenciesRatesUseCase {
        return GetLatestCurrenciesRatesUseCase {
            repository.getLatestCurrenciesRatesCurrencies()
        }
    }

    @Provides
    fun provideGetCurrencyDetailsUseCase(repository: CurrenciesRepository): GetCurrencyDetailsUseCase {
        return GetCurrencyDetailsUseCase {
            repository.getCurrencyDetails(it)
        }
    }
}