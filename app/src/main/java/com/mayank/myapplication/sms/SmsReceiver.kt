package com.mayank.myapplication.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import com.mayank.myapplication.FinanceApp
import com.mayank.myapplication.data.SmsParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (!com.mayank.myapplication.FeatureFlags.useSms) return
        val bundle: Bundle? = intent.extras
        val pdus = bundle?.get("pdus") as? Array<*> ?: return
        for (pdu in pdus) {
            val msg = SmsMessage.createFromPdu(pdu as ByteArray, bundle.getString("format"))
            val app = context.applicationContext as FinanceApp
            val body = msg.messageBody
            val date = msg.timestampMillis
            // Generate a stable identifier for this SMS since SmsMessage does not
            // expose a message ID for received broadcasts on all API levels.
            val id = "${msg.originatingAddress}-${msg.timestampMillis}-${body.hashCode()}"
            SmsParser.parse(body, date, id)?.let { spending ->
                CoroutineScope(Dispatchers.IO).launch {
                    app.repository.insert(spending)
                }
            }
        }
    }
}
