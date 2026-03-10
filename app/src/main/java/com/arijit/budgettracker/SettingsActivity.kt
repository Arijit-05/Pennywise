package com.arijit.budgettracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.content.FileProvider
import com.arijit.budgettracker.utils.Vibration
import com.arijit.budgettracker.utils.CurrencyPrefs
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.arijit.budgettracker.db.ExpenseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsActivity : AppCompatActivity() {
    private lateinit var currency: CardView
    private lateinit var github: CardView
    private lateinit var projects: CardView
    private lateinit var export: CardView
    private var currencySelected = "₹"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        currency = findViewById(R.id.currency)
        currencySelected = CurrencyPrefs.getSymbol(this)
        findViewById<TextView>(R.id.curr).text = currencySelected
        currency.setOnClickListener {
            Vibration.vibrate(this, 50)
            val bottomSheet = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.currency_layout, null)
            bottomSheet.setContentView(view)
            bottomSheet.show()

            val categoriesMap = mapOf(
                R.id.inr to "₹",
                R.id.usd to "$",
                R.id.cny to "¥",
                R.id.jpy to "¥",
                R.id.rub to "₽",
                R.id.eur to "€"
            )

            for ((viewId, categoryName) in categoriesMap) {
                view.findViewById<TextView>(viewId).setOnClickListener {
                    Vibration.vibrate(this, 50)
                    currencySelected = categoryName
                    CurrencyPrefs.setSymbol(this, currencySelected)
                    bottomSheet.dismiss()
                    findViewById<TextView>(R.id.curr).text = currencySelected
                }
            }
        }

        github = findViewById(R.id.github)
        github.setOnClickListener {
            Vibration.vibrate(this, 50)
            val url = "https://github.com/Arijit-05/Pennywise"
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(intent)
        }

        projects = findViewById(R.id.projects)
        projects.setOnClickListener {
            Vibration.vibrate(this, 50)
            val url = "https://arijit-05.github.io/website/"
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(intent)
        }

        export = findViewById(R.id.export)
        export.setOnClickListener {
            Vibration.vibrate(this, 50)
            exportDataToCsv()
        }
    }

    private fun exportDataToCsv() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val db = ExpenseDatabase.getDatabase(applicationContext)
                val expenses = db.expenseDao().getAllExpensesOnce()

                if (expenses.isEmpty()) {
                    launch(Dispatchers.Main) {
                        Toast.makeText(this@SettingsActivity, "No expenses to export", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "pennywise_expenses_$timeStamp.csv"
                val dir = File(cacheDir, "exports")
                if (!dir.exists()) dir.mkdirs()
                val file = File(dir, fileName)

                FileWriter(file).use { writer ->
                    writer.appendLine("id,amount,category,note,date")
                    val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
                    expenses.forEach { e ->
                        val safeNote = (e.note ?: "").replace("\"", "\"\"")
                        val formattedDate = dateFormat.format(Date(e.timeStamp))
                        writer.appendLine("${e.id},${e.amount},\"${e.category}\",\"$safeNote\",\"$formattedDate\"")
                    }
                }

                val uri = FileProvider.getUriForFile(
                    this@SettingsActivity,
                    "${packageName}.fileprovider",
                    file
                )

                launch(Dispatchers.Main) {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/csv"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    startActivity(Intent.createChooser(shareIntent, "Export expenses"))
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    Toast.makeText(this@SettingsActivity, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}