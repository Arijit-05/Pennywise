package com.arijit.budgettracker.models

import com.arijit.budgettracker.db.Expense

data class ExpenseGroup(
    val date: String,
    val expenses: List<Expense>
)