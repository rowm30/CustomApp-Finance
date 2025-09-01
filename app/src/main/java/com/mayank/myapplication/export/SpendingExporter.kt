package com.mayank.myapplication.export

import android.content.Context
import com.mayank.myapplication.data.SpendingDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object SpendingExporter {
    suspend fun exportCsv(context: Context, dao: SpendingDao): File = withContext(Dispatchers.IO) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val now = java.time.ZonedDateTime.now(ZoneId.systemDefault())
        val start = now.minusMonths(1).withDayOfMonth(1).toInstant().toEpochMilli()
        val end = now.plusDays(1).toInstant().toEpochMilli()
        val list = dao.spendingsBetween(start, end).first()
        val file = File(context.filesDir, "spending.csv")
        FileWriter(file).use { writer ->
            writer.appendLine("amount,description,timestamp")
            list.forEach { s ->
                val time = java.time.Instant.ofEpochMilli(s.timestamp).atZone(ZoneId.systemDefault()).format(formatter)
                writer.appendLine("${s.amount},${s.description},$time")
            }
        }
        file
    }

    fun exportDb(context: Context): File {
        val dbFile = context.getDatabasePath("spending.db")
        val dest = File(context.filesDir, "spending.db")
        dbFile.copyTo(dest, overwrite = true)
        return dest
    }
}
