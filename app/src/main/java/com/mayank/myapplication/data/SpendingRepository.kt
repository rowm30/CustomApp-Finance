package com.mayank.myapplication.data

import android.content.Context
import android.net.Uri
import android.provider.Telephony
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class SpendingRepository(private val dao: SpendingDao) {

    suspend fun insert(spending: Spending) = dao.insert(spending)

    fun spendingsForMonth(month: Int, year: Int) = dao.spendingsBetween(
        monthStart(month, year), monthEnd(month, year)
    )

    fun totalForMonth(month: Int, year: Int) = dao.totalBetween(
        monthStart(month, year), monthEnd(month, year)
    )

    suspend fun ingestSmsSince(context: Context, since: Long) = withContext(Dispatchers.IO) {
        val uri: Uri = Telephony.Sms.Inbox.CONTENT_URI
        val projection = arrayOf(Telephony.Sms._ID, Telephony.Sms.BODY, Telephony.Sms.DATE)
        val selection = "${Telephony.Sms.DATE} >= ?"
        val args = arrayOf(since.toString())
        context.contentResolver.query(uri, projection, selection, args, null)?.use { cursor ->
            val idIdx = cursor.getColumnIndex(Telephony.Sms._ID)
            val bodyIdx = cursor.getColumnIndex(Telephony.Sms.BODY)
            val dateIdx = cursor.getColumnIndex(Telephony.Sms.DATE)
            while (cursor.moveToNext()) {
                val body = cursor.getString(bodyIdx)
                val date = cursor.getLong(dateIdx)
                val smsId = cursor.getString(idIdx)
                SmsParser.parse(body, date, smsId)?.let { dao.insert(it) }
            }
        }
    }

    private fun monthStart(month: Int, year: Int): Long {
        val dt = ZonedDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneId.systemDefault())
        return dt.toInstant().toEpochMilli()
    }

    private fun monthEnd(month: Int, year: Int): Long {
        val start = ZonedDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneId.systemDefault())
        val end = start.plusMonths(1)
        return end.toInstant().toEpochMilli() - 1
    }
}

object SmsParser {
    private val regex = Regex("INR\\s+([\\d,.]+).*on\\s+(\\d{2}-\\d{2}-\\d{4})\\s+(\\d{2}:\\d{2})\\s*(AM|PM)", RegexOption.IGNORE_CASE)
    private val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a", Locale.ENGLISH)

    fun parse(body: String, date: Long? = null, sourceId: String? = null): Spending? {
        val match = regex.find(body) ?: return null
        val amount = match.groupValues[1].replace(",", "").toDoubleOrNull() ?: return null
        val dateStr = match.groupValues[2] + " " + match.groupValues[3] + " " + match.groupValues[4]
        val timestamp = date ?: run {
            val ldt = java.time.LocalDateTime.parse(dateStr, formatter)
            ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }
        return Spending(amount = amount, description = body.take(100), timestamp = timestamp, sourceId = sourceId)
    }
}
