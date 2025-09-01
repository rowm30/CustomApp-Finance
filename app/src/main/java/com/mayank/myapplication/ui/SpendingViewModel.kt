package com.mayank.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mayank.myapplication.data.Spending
import com.mayank.myapplication.data.SpendingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

class SpendingViewModel(private val repo: SpendingRepository) : ViewModel() {
    private val now = LocalDate.now()
    private val month = now.monthValue
    private val year = now.year

    val spendings: StateFlow<List<Spending>> = repo.spendingsForMonth(month, year)
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val total: StateFlow<Double> = repo.totalForMonth(month, year)
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)
}
