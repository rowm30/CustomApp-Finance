package com.mayank.myapplication

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.mayank.myapplication.sms.SmsIngestWorker

object SmsIngestScheduler {
    fun schedule(context: Context) {
        val request = OneTimeWorkRequestBuilder<SmsIngestWorker>().build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "sms-ingest",
            ExistingWorkPolicy.KEEP,
            request
        )
    }
}
