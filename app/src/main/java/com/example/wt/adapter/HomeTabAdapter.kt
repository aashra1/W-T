package com.example.wt.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.wt.ui.fragment.FeaturedFragment
import com.example.wt.ui.fragment.NewArrivalsFragment

class HomeTabAdapter (
    fragment: Fragment
) : FragmentStateAdapter(fragment){

    override fun getItemCount(): Int {
        return 2;
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return FeaturedFragment()
            1 -> return NewArrivalsFragment()
            else -> return FeaturedFragment()
        }
    }
}