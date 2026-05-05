package com.rapi.pocketwise.data.repository

import com.rapi.pocketwise.data.model.Expense
import com.rapi.pocketwise.data.model.FinanceSummary

interface FinanceRepository {
    fun calculateSummary(expenses: List<Expense>): FinanceSummary
    suspend fun analyzeExpenses(expenses: List<Expense>): Result<String>
}