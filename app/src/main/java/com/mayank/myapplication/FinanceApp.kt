package com.mayank.myapplication

import android.app.Application
import androidx.room.Room
import com.mayank.myapplication.data.SpendingDatabase
import com.mayank.myapplication.data.SpendingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class FinanceApp : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    val database: SpendingDatabase by lazy {
        Room.databaseBuilder(
            this,
            SpendingDatabase::class.java,
            SpendingDatabase.NAME
        ).build()
    }

    val repository: SpendingRepository by lazy {
        SpendingRepository(database.spendingDao())
    }

    override fun onCreate() {
        super.onCreate()
        SmsIngestScheduler.schedule(applicationContext)
    }
}
