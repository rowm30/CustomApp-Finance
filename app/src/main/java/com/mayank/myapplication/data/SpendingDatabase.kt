package com.mayank.myapplication.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Spending::class], version = 1)
abstract class SpendingDatabase : RoomDatabase() {
    abstract fun spendingDao(): SpendingDao

    companion object {
        const val NAME = "spending.db"
    }
}
