package com.rapi.pocketwise.data.model

data class Expense(
    val id: String,
    val amount: Int,
    val category: ExpenseCategory,
    val note: String,
    val date: String
)

enum class ExpenseCategory(
    val label: String
) {
    FOOD("Makanan"),
    TRANSPORT("Transportasi"),
    COFFEE("Kopi/Jajan"),
    EDUCATION("Pendidikan"),
    SHOPPING("Belanja"),
    OTHER("Lainnya")
}