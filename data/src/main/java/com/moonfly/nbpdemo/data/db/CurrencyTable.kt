package com.moonfly.nbpdemo.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_table")
data class CurrencyTable(
    @PrimaryKey val code: String,
    val table: String
)