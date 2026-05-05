package com.rapi.pocketwise.data.model

data class FinanceSummary(
    val totalExpense: Int,
    val highestCategory: String,
    val categoryBreakdown: String,
    val expenseCount: Int
)