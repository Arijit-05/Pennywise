package com.arijit.budgettracker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.arijit.budgettracker.utils.Vibration
import com.arijit.budgettracker.utils.CurrencyPrefs
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.core.net.toUri

class SettingsActivity : AppCompatActivity() {
    private lateinit var currency: CardView
    private lateinit var github: CardView
    private lateinit var projects: CardView
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
        // Load saved currency and show it
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
            val url = "https://github.com/Arijit-05"
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
    }
}