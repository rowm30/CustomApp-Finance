package com.mayank.myapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SpendingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(spending: Spending)

    @Query("SELECT * FROM spending WHERE timestamp BETWEEN :start AND :end ORDER BY timestamp DESC")
    fun spendingsBetween(start: Long, end: Long): Flow<List<Spending>>

    @Query("SELECT SUM(amount) FROM spending WHERE timestamp BETWEEN :start AND :end")
    fun totalBetween(start: Long, end: Long): Flow<Double?>
}
