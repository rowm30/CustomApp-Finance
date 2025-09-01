package com.mayank.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.mayank.myapplication.ui.SpendingScreen
import com.mayank.myapplication.ui.SpendingViewModel
import com.mayank.myapplication.ui.SpendingViewModelFactory

class MainActivity : ComponentActivity() {
    private val viewModel: SpendingViewModel by viewModels {
        SpendingViewModelFactory(application as FinanceApp)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    SpendingScreen(viewModel)
                }
            }
        }
    }
}
