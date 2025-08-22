package com.arijit.budgettracker

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.arijit.budgettracker.utils.Vibration
import com.arijit.budgettracker.utils.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: ViewPagerAdapter
    private lateinit var headerTxt: TextView
    private lateinit var settings: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewPager = findViewById(R.id.pager)
        adapter = ViewPagerAdapter(this)
        headerTxt = findViewById(R.id.header_txt)
        settings = findViewById(R.id.settings)

        settings.setOnClickListener {
            Vibration.vibrate(this, 50)
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
        }

        viewPager.adapter = adapter

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val tabIcons = arrayOf(R.drawable.home, R.drawable.history, R.drawable.stats)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.setIcon(tabIcons[position])
        }.attach()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                headerTxt.animate()
                    .alpha(0f)
                    .setDuration(100)
                    .withEndAction {
                        headerTxt.text = when (tab?.position) {
                            0 -> "Home"
                            1 -> "History"
                            2 -> "Stats"
                            else -> "Home"
                        }
                        headerTxt.animate()
                            .alpha(1f)
                            .setDuration(100)
                            .start()
                    }.start()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
}