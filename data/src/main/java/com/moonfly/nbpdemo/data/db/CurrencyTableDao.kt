package com.moonfly.nbpdemo.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CurrencyTableDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencyTable(currencyTable: CurrencyTable)

    @Query("SELECT * FROM currency_table WHERE code = :code LIMIT 1")
    suspend fun getCurrencyTableByCode(code: String): CurrencyTable
}