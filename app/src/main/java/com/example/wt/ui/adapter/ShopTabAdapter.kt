package com.example.wt.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.wt.ui.fragment.BrandsFragment
import com.example.wt.ui.fragment.BrowseFragment

class ShopTabAdapter (
    fragment: Fragment
) : FragmentStateAdapter(fragment){

    override fun getItemCount(): Int {
        return 2;
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return BrowseFragment()
            1 -> return BrandsFragment()
            else -> return BrowseFragment()
        }
    }
}