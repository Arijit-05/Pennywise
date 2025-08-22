package com.arijit.budgettracker.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.arijit.budgettracker.db.Expense
import com.arijit.budgettracker.db.ExpenseDatabase
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.exp

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val expenseDao = ExpenseDatabase.Companion.getDatabase(application).expenseDao()
    val latestExpenses: LiveData<List<Expense>> = expenseDao.getLatest8Expenses()
    val allExpenses: LiveData<List<Expense>> = expenseDao.getAllExpensesFlow().asLiveData()
    private val _todayAmount = MutableLiveData<Double>()
    val todayAmount: LiveData<Double> = _todayAmount
    private val _weekAmount = MutableLiveData<Double>()
    val weekAmount: LiveData<Double> = _weekAmount
    private val _monthAmount = MutableLiveData<Double>()
    val monthAmount: LiveData<Double> = _monthAmount

    init {
        viewModelScope.launch {
            expenseDao.getAllExpensesFlow().collect { expenses ->
                val now = System.currentTimeMillis()
                val startOfDay = getStartOfDay(now)
                val startOfWeek = getStartOfWeek(now)
                val startOfMonth = getStartOfMonth(now)

                _todayAmount.value = expenses.filter { it.timeStamp >= startOfDay }.sumOf { it.amount }
                _weekAmount.value = expenses.filter { it.timeStamp >= startOfWeek }.sumOf { it.amount }
                _monthAmount.value = expenses.filter { it.timeStamp >= startOfMonth }.sumOf { it.amount }
            }
        }
    }

    private fun getStartOfDay(now: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = now
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    private fun getStartOfWeek(now: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = now
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    private fun getStartOfMonth(now: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = now
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }


    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            expenseDao.deleteExpense(expense)
        }
    }
}