package com.arijit.budgettracker.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arijit.budgettracker.AddExpenseActivity
import com.arijit.budgettracker.R
import com.arijit.budgettracker.utils.ExpenseAdapter
import com.arijit.budgettracker.models.HomeViewModel
import com.arijit.budgettracker.utils.Vibration
import com.arijit.budgettracker.utils.CurrencyPrefs

class HomeFragment : Fragment() {
    private lateinit var todayAmt: TextView
    private lateinit var thisWeekAmt: TextView
    private lateinit var thisMonthAmt: TextView
    private lateinit var expenseBtn: CardView
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: ExpenseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        todayAmt = view.findViewById(R.id.today_amt)
        thisWeekAmt = view.findViewById(R.id.this_week_amt)
        thisMonthAmt = view.findViewById(R.id.this_month_amt)
        expenseBtn = view.findViewById(R.id.add_expense_btn)
        expenseBtn.setOnClickListener {
            Vibration.vibrate(requireContext(), 100)
            startActivity(Intent(requireContext(), AddExpenseActivity::class.java))
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recent_rv)
        adapter = ExpenseAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter.onItemLongClick = { expense ->
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Expense")
                .setMessage("Are you sure you want to delete this expense?")
                .setPositiveButton("Delete") { _, _ ->
                    viewModel.deleteExpense(expense)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        viewModel.latestExpenses.observe(viewLifecycleOwner) { expenses ->
            adapter.submitList(expenses)
        }


        viewModel.todayAmount.observe(viewLifecycleOwner) {
            val sym = CurrencyPrefs.getSymbol(requireContext())
            todayAmt.text = "$sym%.2f".format(it)
        }

        viewModel.weekAmount.observe(viewLifecycleOwner) {
            val sym = CurrencyPrefs.getSymbol(requireContext())
            thisWeekAmt.text = "$sym%.2f".format(it)
        }

        viewModel.monthAmount.observe(viewLifecycleOwner) {
            val sym = CurrencyPrefs.getSymbol(requireContext())
            thisMonthAmt.text = "$sym%.2f".format(it)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // Refresh amounts and list to reflect currency changes
        val sym = CurrencyPrefs.getSymbol(requireContext())
        viewModel.todayAmount.value?.let { todayAmt.text = "$sym%.2f".format(it) }
        viewModel.weekAmount.value?.let { thisWeekAmt.text = "$sym%.2f".format(it) }
        viewModel.monthAmount.value?.let { thisMonthAmt.text = "$sym%.2f".format(it) }
        adapter.notifyDataSetChanged()
    }
}