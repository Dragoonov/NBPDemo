package com.moonfly.nbpdemo.di

import android.content.Context
import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.moonfly.nbpdemo.BuildConfig
import com.moonfly.nbpdemo.data.ApiHelper
import com.moonfly.nbpdemo.data.CurrenciesDataSourceImpl
import com.moonfly.nbpdemo.data.CurrenciesService
import com.moonfly.nbpdemo.data.CurrencyTableCache
import com.moonfly.nbpdemo.data.db.CurrencyTableDao
import com.moonfly.nbpdemo.data.db.NBPDemoDatabase
import com.moonfly.nbpdemo.domain.repository.CurrenciesDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NBPDemoDatabase {
        return Room.databaseBuilder(
            context,
            NBPDemoDatabase::class.java,
            "nbp_demo_database"
        ).build()
    }

    @Provides
    fun provideCurrencyTableDao(database: NBPDemoDatabase): CurrencyTableDao {
        return database.currencyTableDao()
    }

    @Provides
    fun provideCurrencyTableCache(currencyTableDao: CurrencyTableDao): CurrencyTableCache {
        return CurrencyTableCache(currencyTableDao)
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideCurrenciesService(retrofit: Retrofit): CurrenciesService {
        return retrofit.create(CurrenciesService::class.java)
    }

    @Provides
    fun provideApiHelper(): ApiHelper = ApiHelper

    @Provides
    fun provideCurrenciesDataSource(
        currenciesService: CurrenciesService,
        apiHelper: ApiHelper,
        currencyTableCache: CurrencyTableCache
    ): CurrenciesDataSource {
        return CurrenciesDataSourceImpl(currenciesService, apiHelper, currencyTableCache)
    }
}