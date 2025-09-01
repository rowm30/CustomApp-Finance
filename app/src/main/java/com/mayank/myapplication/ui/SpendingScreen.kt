package com.mayank.myapplication.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mayank.myapplication.FinanceApp
import com.mayank.myapplication.export.SpendingExporter
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpendingScreen(viewModel: SpendingViewModel) {
    val spendings by viewModel.spendings.collectAsState()
    val total by viewModel.total.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")

    Scaffold(topBar = {
        TopAppBar(title = { Text("Spending") }, actions = {
            TextButton(onClick = {
                val app = context.applicationContext as FinanceApp
                scope.launch {
                    SpendingExporter.exportCsv(context, app.database.spendingDao())
                }
            }) { Text("CSV") }
            TextButton(onClick = {
                val app = context.applicationContext as FinanceApp
                SpendingExporter.exportDb(context)
            }) { Text("DB") }
        })
    }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text(text = "Total this month: ₹$total", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(spendings) { s ->
                    val time = Instant.ofEpochMilli(s.timestamp).atZone(ZoneId.systemDefault()).format(formatter)
                    Text(text = "₹${s.amount} - $time")
                    Text(text = s.description, style = MaterialTheme.typography.bodySmall)
                    Divider()
                }
            }
        }
    }
}
