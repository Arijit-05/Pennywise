package com.arijit.budgettracker.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.arijit.budgettracker.fragments.HistoryFragment
import com.arijit.budgettracker.fragments.HomeFragment
import com.arijit.budgettracker.fragments.StatsFragment

class ViewPagerAdapter(activity: FragmentActivity): FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment {
        return when (position)  {
            0 -> HomeFragment()
            1 -> HistoryFragment()
            2 -> StatsFragment()
            else -> HomeFragment()
        }
    }

    override fun getItemCount(): Int = 3

}