package com.arijit.budgettracker.utils

import java.util.*

fun Date.isToday(): Boolean {
    val cal1 = Calendar.getInstance()
    val cal2 = Calendar.getInstance().apply { time = this@isToday }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

fun Date.isThisWeek(): Boolean {
    val cal1 = Calendar.getInstance()
    val cal2 = Calendar.getInstance().apply { time = this@isThisWeek }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)
}

fun Date.isThisMonth(): Boolean {
    val cal1 = Calendar.getInstance()
    val cal2 = Calendar.getInstance().apply { time = this@isThisMonth }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
}
