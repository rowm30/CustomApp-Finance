package com.mayank.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spending")
data class Spending(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val description: String,
    val timestamp: Long,
    val sourceId: String? = null
)
