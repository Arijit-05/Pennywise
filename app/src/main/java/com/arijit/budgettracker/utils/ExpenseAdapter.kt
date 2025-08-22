package com.arijit.budgettracker.utils

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arijit.budgettracker.R
import com.arijit.budgettracker.db.Expense
import java.text.SimpleDateFormat
import java.util.*

class ExpenseAdapter(
    var onItemLongClick: ((Expense) -> Unit)? = null
) : ListAdapter<Expense, ExpenseAdapter.ExpenseViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Expense>() {
            override fun areItemsTheSame(oldItem: Expense, newItem: Expense): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Expense, newItem: Expense): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewAmount: TextView = itemView.findViewById(R.id.textViewAmount)
        val textViewCategory: TextView = itemView.findViewById(R.id.textViewCategory)

        fun bind(expense: Expense) {
            val sym = CurrencyPrefs.getSymbol(itemView.context)
            textViewAmount.text = "$sym${expense.amount}"
            textViewCategory.text = expense.category
            itemView.setOnLongClickListener {
                onItemLongClick?.invoke(expense)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
