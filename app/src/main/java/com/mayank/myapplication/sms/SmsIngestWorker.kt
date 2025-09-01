package com.mayank.myapplication.sms

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mayank.myapplication.FinanceApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import java.time.ZoneId

class SmsIngestWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val app = applicationContext as FinanceApp
        val start = ZonedDateTime.now(ZoneId.systemDefault()).withDayOfMonth(1).minusMonths(1)
            .toInstant().toEpochMilli()
        app.repository.ingestSmsSince(applicationContext, start)
        Result.success()
    }
}
