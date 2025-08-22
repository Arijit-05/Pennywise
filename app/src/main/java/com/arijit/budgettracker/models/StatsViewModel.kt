package com.arijit.budgettracker.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.arijit.budgettracker.db.Expense
import com.arijit.budgettracker.db.ExpenseDatabase

class StatsViewModel(application: Application) : AndroidViewModel(application) {
    private val expenseDao = ExpenseDatabase.getDatabase(application).expenseDao()

    val allExpenses: LiveData<List<Expense>> = expenseDao.getAllExpensesFlow().asLiveData()
}
