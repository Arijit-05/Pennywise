package com.arijit.budgettracker.fragments

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.arijit.budgettracker.R
import com.arijit.budgettracker.db.Expense
import com.arijit.budgettracker.models.StatsViewModel
import com.arijit.budgettracker.utils.RoundedBarChartRenderer
import com.arijit.budgettracker.utils.CurrencyPrefs
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class StatsFragment : Fragment() {
    private lateinit var barChart: BarChart
    private lateinit var pieChart: PieChart
    private lateinit var mostSpentTxt: TextView
    private lateinit var viewModel: StatsViewModel
    private var lastExpenses: List<Expense> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stats, container, false)
        barChart = view.findViewById(R.id.daily_expense)
        barChart.setRenderer(
            RoundedBarChartRenderer(barChart, barChart.animator, barChart.viewPortHandler)
        )
        pieChart = view.findViewById(R.id.pie_chart)
        mostSpentTxt = view.findViewById(R.id.most_spent_txt)
        viewModel = ViewModelProvider(this)[StatsViewModel::class.java]
        viewModel.allExpenses.observe(viewLifecycleOwner) { expenses ->
            if (expenses != null) {
                lastExpenses = expenses
                loadBarChart(expenses)
                showMostSpentDayThisMonth(expenses)
                loadCategoryPieChart(expenses)
            }
        }

        return view
    }

    private fun loadBarChart(expenses: List<Expense>) {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
        var typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_regular)
        val tf = ResourcesCompat.getFont(requireContext(), R.font.montserrat_regular)

        val last7DaysLabels = mutableListOf<String>()
        val dailyTotals = LinkedHashMap<String, Float>()

        // Initialize 0 for all 7 days
        for (i in 6 downTo 0) {
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val label = dateFormat.format(calendar.time)
            last7DaysLabels.add(label)
            dailyTotals[label] = 0f
        }

        for (expense in expenses) {
            val expenseCal = Calendar.getInstance()
            expenseCal.timeInMillis = expense.timeStamp
            val dateLabel = dateFormat.format(expenseCal.time)

            if (dateLabel in dailyTotals) {
                dailyTotals[dateLabel] = (dailyTotals[dateLabel]!! + expense.amount).toFloat()
            }
        }

        val barEntries = ArrayList<BarEntry>()
        for ((index, amount) in dailyTotals.values.withIndex()) {
            barEntries.add(BarEntry(index.toFloat(), amount))
        }

        val barDataSet = BarDataSet(barEntries, "")
        barDataSet.color = Color.parseColor("#4CAF50")
        barDataSet.valueTextColor = Color.BLACK
        barDataSet.valueTextSize = 12f

        val data = BarData(barDataSet)
        barChart.data = data

        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault()) // "Mon", "Tue", etc.
        val weekDayLabels = mutableListOf<String>()

        val tempCal = Calendar.getInstance()
        for (i in 6 downTo 0) {
            tempCal.timeInMillis = System.currentTimeMillis()
            tempCal.add(Calendar.DAY_OF_YEAR, -i)
            weekDayLabels.add(dayFormat.format(tempCal.time))
        }

        barChart.setExtraOffsets(10f, 10f, 10f, 30f)
        data.setValueTextColor(Color.BLACK)
        data.setValueTypeface(tf)

        barChart.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(weekDayLabels)
            position = XAxis.XAxisPosition.BOTTOM
            textColor = Color.BLACK
            textSize = 12f
            typeface = tf
            granularity = 1f
            setDrawGridLines(false)
            setDrawAxisLine(false)
            labelRotationAngle = 0f
        }

        val yAxis = barChart.axisLeft
        yAxis.setDrawGridLines(false)
        yAxis.setDrawAxisLine(false)
        yAxis.typeface = tf
        yAxis.textColor = Color.BLACK
        yAxis.textSize = 12f
        yAxis.axisMinimum = 0f

        barChart.axisRight.isEnabled = false
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.setScaleEnabled(false)
        barChart.setFitBars(true)
        barChart.animateY(800)
        barChart.invalidate()

    }

    private fun showMostSpentDayThisMonth(expenses: List<Expense>) {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        val monthlyExpenses = expenses.filter {
            val expCal = Calendar.getInstance().apply { timeInMillis = it.timeStamp }
            expCal.get(Calendar.MONTH) == currentMonth && expCal.get(Calendar.YEAR) == currentYear
        }

        if (monthlyExpenses.isEmpty()) {
            mostSpentTxt.text = "No expenses this month"
            return
        }

        // Group by date and sum amounts
        val grouped = monthlyExpenses.groupBy {
            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            sdf.format(Date(it.timeStamp))
        }.mapValues { entry ->
            entry.value.sumOf { it.amount }
        }

        val (maxDateStr, maxAmt) = grouped.maxByOrNull { it.value } ?: return

        // Convert date to readable format like "3rd Aug"
        val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(maxDateStr)!!
        val day = SimpleDateFormat("d", Locale.getDefault()).format(date)
        val month = SimpleDateFormat("MMM", Locale.getDefault()).format(date)

        val suffix = when {
            day.endsWith("1") && day != "11" -> "st"
            day.endsWith("2") && day != "12" -> "nd"
            day.endsWith("3") && day != "13" -> "rd"
            else -> "th"
        }

        val dayWithSuffix = "$day$suffix $month"
        val sym = CurrencyPrefs.getSymbol(requireContext())
        val formattedAmount = "$sym${"%,.0f".format(maxAmt)}"

        mostSpentTxt.text = "$dayWithSuffix - $formattedAmount"
    }

    private fun loadCategoryPieChart(expenses: List<Expense>) {
        val tf = ResourcesCompat.getFont(requireContext(), R.font.montserrat_regular)
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        // Filter expenses for current month
        val monthlyExpenses = expenses.filter {
            val cal = Calendar.getInstance().apply { timeInMillis = it.timeStamp }
            cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear
        }

        // Group by category and sum amounts
        val categoryTotals = monthlyExpenses.groupBy { it.category }
            .mapValues { it.value.sumOf { exp -> exp.amount } }

        if (categoryTotals.isEmpty()) {
            pieChart.clear()
            pieChart.centerText = "No Data"
            pieChart.invalidate()
            return
        }

        val entries = ArrayList<PieEntry>()
        categoryTotals.forEach { (category, amount) ->
            entries.add(PieEntry(amount.toFloat(), category))
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        val currentMonthCenter = SimpleDateFormat("MMMM", Locale.getDefault()).format(Date())
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

        val data = PieData(dataSet)
        data.setDrawValues(false)

        pieChart.data = data
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.setDrawHoleEnabled(true)
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.setTransparentCircleAlpha(0)
        pieChart.setCenterText(currentMonthCenter)
        pieChart.setCenterTextTypeface(tf)
        pieChart.setCenterTextSize(16f)
        pieChart.setCenterTextColor(Color.BLACK)
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.setEntryLabelTextSize(12f)
        pieChart.setDrawEntryLabels(false)

        val legend = pieChart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)
        legend.textColor = Color.BLACK
        legend.typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_regular)
        legend.textSize = 12f
        legend.isWordWrapEnabled = true
        legend.yOffset = 10f
        legend.xOffset = -20f
        legend.form = Legend.LegendForm.CIRCLE
        legend.xEntrySpace = 25f  // horizontal space between entries
        legend.yEntrySpace = 10f

        // Animate
        pieChart.animateY(1000, Easing.EaseInOutQuad)
        pieChart.invalidate()
    }
}
