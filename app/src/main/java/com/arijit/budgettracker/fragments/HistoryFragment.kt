package com.arijit.budgettracker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arijit.budgettracker.R
import com.arijit.budgettracker.db.Expense
import com.arijit.budgettracker.models.DailyExpense
import com.arijit.budgettracker.models.HomeViewModel
import com.arijit.budgettracker.utils.HistoryAdapter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryFragment : Fragment() {
    private lateinit var viewModel: HomeViewModel
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var containerRv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        containerRv = view.findViewById(R.id.container_rv)
        historyAdapter = HistoryAdapter()

        containerRv.layoutManager = LinearLayoutManager(requireContext())
        containerRv.adapter = historyAdapter

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        viewModel.allExpenses.observe(viewLifecycleOwner) { expenses ->
            val grouped = groupExpensesByDate(expenses)
            historyAdapter.submitList(grouped)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // Rebind to apply current currency symbol in nested expense items
        historyAdapter.notifyDataSetChanged()
    }

    private fun groupExpensesByDate(expenses: List<Expense>): List<DailyExpense> {
        val sdf = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        return expenses
            .groupBy { sdf.format(Date(it.timeStamp)) }
            .map { entry -> 
                DailyExpense(
                    entry.key, 
                    entry.value.sortedByDescending { it.timeStamp }
                ) 
            }
            .sortedByDescending { dailyExpense ->
                dailyExpense.expenses.firstOrNull()?.timeStamp ?: 0L
            }
    }
}