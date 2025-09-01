package com.mayank.myapplication.sms

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.mayank.myapplication.FinanceApp
import com.mayank.myapplication.FeatureFlags
import com.mayank.myapplication.data.SmsParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BankNotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (FeatureFlags.useSms) return
        val text = sbn.notification.extras.getCharSequence("android.text")?.toString() ?: return
        val app = application as FinanceApp
        SmsParser.parse(text)?.let { spending ->
            CoroutineScope(Dispatchers.IO).launch {
                app.repository.insert(spending)
            }
        }
    }
}
