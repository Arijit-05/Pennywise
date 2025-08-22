package com.arijit.budgettracker.utils

import android.content.Context

object CurrencyPrefs {
    private const val PREFS_NAME = "budget_prefs"
    private const val KEY_CURRENCY_SYMBOL = "currency_symbol"
    private const val DEFAULT_SYMBOL = "₹"

    fun getSymbol(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_CURRENCY_SYMBOL, DEFAULT_SYMBOL) ?: DEFAULT_SYMBOL
    }

    fun setSymbol(context: Context, symbol: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_CURRENCY_SYMBOL, symbol).apply()
    }
}


