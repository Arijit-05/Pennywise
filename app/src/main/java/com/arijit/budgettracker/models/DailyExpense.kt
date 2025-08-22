package com.arijit.budgettracker.models

import com.arijit.budgettracker.db.Expense

data class DailyExpense (
    val date: String,
    val expenses: List<Expense>
)