package com.arijit.budgettracker

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.arijit.budgettracker.db.Expense
import com.arijit.budgettracker.db.ExpenseDatabase
import com.arijit.budgettracker.utils.Vibration
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch

class AddExpenseActivity : AppCompatActivity() {
    private lateinit var cancelBtn: ImageView
    private lateinit var tickBtn: ImageView
    private lateinit var category: CardView
    private lateinit var expenseAmount: EditText
    private var catgSelected: String = ""
    private var finalExpenseAmt: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_expense)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        expenseAmount = findViewById(R.id.expense_amt)
        cancelBtn = findViewById(R.id.close)
        cancelBtn.setOnClickListener {
            Vibration.vibrate(this, 100)
            finish()
        }

        setupKeypad()

        category = findViewById(R.id.catg_btn)
        category.setOnClickListener {
            Vibration.vibrate(this, 50)
            val bottomSheet = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.catg_layout, null)
            bottomSheet.setContentView(view)
            bottomSheet.show()

            val categoriesMap = mapOf(
                R.id.transport to "Transport",
                R.id.entertainment to "Entertainment",
                R.id.food to "Food",
                R.id.housing to "Housing",
                R.id.pet to "Pet",
                R.id.health to "Health",
                R.id.shopping to "Shopping",
                R.id.miscellaneous to "Miscellaneous"
            )

            for ((viewId, categoryName) in categoriesMap) {
                view.findViewById<LinearLayout>(viewId).setOnClickListener {
                    catgSelected = categoryName
                    bottomSheet.dismiss()
                    findViewById<TextView>(R.id.catg_txt).text = catgSelected
                }
            }

        }

        tickBtn = findViewById(R.id.tick)
        tickBtn.setOnClickListener {
            Vibration.vibrate(this, 100)
            finalExpenseAmt = expenseAmount.text.toString()

            if (finalExpenseAmt != "" && catgSelected != "") {
                lifecycleScope.launch {
                    val db = ExpenseDatabase.getDatabase(applicationContext)
                    val dao = db.expenseDao()
                    val expense =
                        Expense(amount = finalExpenseAmt.toDouble(), category = catgSelected)
                    dao.insertExpense(expense)
                    finish()
                }
            }
        }
    }

    private fun setupKeypad() {
        val one = findViewById<TextView>(R.id.btn_1)
        val two = findViewById<TextView>(R.id.btn_2)
        val three = findViewById<TextView>(R.id.btn_3)
        val four = findViewById<TextView>(R.id.btn_4)
        val five = findViewById<TextView>(R.id.btn_5)
        val six = findViewById<TextView>(R.id.btn_6)
        val seven = findViewById<TextView>(R.id.btn_7)
        val eight = findViewById<TextView>(R.id.btn_8)
        val nine = findViewById<TextView>(R.id.btn_9)
        val zero = findViewById<TextView>(R.id.btn_0)
        val backspace = findViewById<TextView>(R.id.btn_clear)
        val dot = findViewById<TextView>(R.id.btn_dot)

        expenseAmount.showSoftInputOnFocus = false
        expenseAmount.isFocusable = false
        expenseAmount.isFocusableInTouchMode = false

        val numberClickListener = View.OnClickListener { v ->
            Vibration.vibrate(this, 50)
            val value = (v as TextView).text.toString()
            val currentText = expenseAmount.text.toString()

            if (value == ".") {
                if (currentText.isEmpty()) return@OnClickListener
                if (currentText.endsWith(".")) return@OnClickListener
                if (currentText.contains(".")) return@OnClickListener
            }

            expenseAmount.append(value)
        }

        one.setOnClickListener(numberClickListener)
        two.setOnClickListener(numberClickListener)
        three.setOnClickListener(numberClickListener)
        four.setOnClickListener(numberClickListener)
        five.setOnClickListener(numberClickListener)
        six.setOnClickListener(numberClickListener)
        seven.setOnClickListener(numberClickListener)
        eight.setOnClickListener(numberClickListener)
        nine.setOnClickListener(numberClickListener)
        zero.setOnClickListener(numberClickListener)
        dot.setOnClickListener(numberClickListener)

        backspace.setOnClickListener {
            Vibration.vibrate(this, 50)
            val text = expenseAmount.text.toString()
            if (text.isNotEmpty()) {
                expenseAmount.setText(text.dropLast(1))
                expenseAmount.setSelection(expenseAmount.text.length)
            }
        }
    }

}