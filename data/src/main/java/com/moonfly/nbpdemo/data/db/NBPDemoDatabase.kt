package com.moonfly.nbpdemo.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CurrencyTable::class], version = 1)
abstract class NBPDemoDatabase : RoomDatabase() {
    abstract fun currencyTableDao(): CurrencyTableDao
}