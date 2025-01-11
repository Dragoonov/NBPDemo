package com.moonfly.nbpdemo.data

import com.moonfly.nbpdemo.data.db.CurrencyTable
import com.moonfly.nbpdemo.data.db.CurrencyTableDao
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test

class CurrencyTableCacheTest {

    @Test
    fun `should successfully save and code with table `() = runTest {
        //Given
        val mockDao = mockk<CurrencyTableDao> {
            coEvery { getCurrencyTableByCode(any()) } returns mockk()
            coEvery { insertCurrencyTable(any()) } returns Unit
        }
        val currencyCode = "PLN"

        //When
        val cache = CurrencyTableCache(mockDao)
        cache.saveCurrencyTable(currencyCode, "Test")

        //Then
        coVerify { mockDao.insertCurrencyTable(CurrencyTable(currencyCode, "Test")) }
    }

    @Test
    fun `should successfully retrieve table from in memory cache `() = runTest {
        //Given
        val mockDao = mockk<CurrencyTableDao> {
            coEvery { getCurrencyTableByCode(any()) } returns mockk()
            coEvery { insertCurrencyTable(any()) } just Runs
        }
        val currencyCode = "PLN"
        val cache = CurrencyTableCache(mockDao)
        cache.saveCurrencyTable(currencyCode, "Test")

        //When
        val result = cache.getTableForCurrency(currencyCode)

        //Then
        coVerify(exactly = 0) { mockDao.getCurrencyTableByCode(currencyCode) }
        assertEquals("Test", result)
    }
}