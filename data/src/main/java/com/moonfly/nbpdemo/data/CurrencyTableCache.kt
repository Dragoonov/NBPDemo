package com.moonfly.nbpdemo.data

import com.moonfly.nbpdemo.data.db.CurrencyTable
import com.moonfly.nbpdemo.data.db.CurrencyTableDao

class CurrencyTableCache(
    private val currencyTableDao: CurrencyTableDao
) {
    private val tablesCache = mutableMapOf<String, String>()

    suspend fun saveCurrencyTable(code: String, table: String) {
        tablesCache[code] = table
        currencyTableDao.insertCurrencyTable(CurrencyTable(code, table))
    }

    suspend fun getTableForCurrency(code: String): String {
        return tablesCache[code] ?: currencyTableDao.getCurrencyTableByCode(code).table
    }
}