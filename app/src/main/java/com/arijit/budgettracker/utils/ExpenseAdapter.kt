package com.arijit.budgettracker.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arijit.budgettracker.R
import com.arijit.budgettracker.db.Expense

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
        val imageViewCategory: ImageView = itemView.findViewById(R.id.catg_img)

        fun bind(expense: Expense) {
            val sym = CurrencyPrefs.getSymbol(itemView.context)
            textViewAmount.text = "$sym${expense.amount}"
            textViewCategory.text = expense.category
            
            // Set category image based on category name
            val categoryImageName = expense.category.lowercase()
            val imageResourceId = getCategoryImageResource(categoryImageName)
            imageViewCategory.setImageResource(imageResourceId)
            
            itemView.setOnLongClickListener {
                onItemLongClick?.invoke(expense)
                true
            }
        }
        
        private fun getCategoryImageResource(categoryName: String): Int {
            return when (categoryName) {
                "food" -> R.drawable.food
                "health" -> R.drawable.health
                "transport" -> R.drawable.transport
                "shopping" -> R.drawable.shopping
                "entertainment" -> R.drawable.entertainment
                "house" -> R.drawable.house
                "pet" -> R.drawable.pet
                "misc" -> R.drawable.misc
                else -> R.drawable.misc // Default fallback
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
