package com.example.wt.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import com.example.wt.R
import com.example.wt.adapter.HomeTabAdapter
import com.example.wt.ui.activity.WishlistActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout: TabLayout = view.findViewById(R.id.tabLayout)
        val viewPager: ViewPager2 = view.findViewById(R.id.viewPager)

        // Data for tab titles
        val data = arrayOf("FEATURED", "NEWARRIVALS")

        val adapter = HomeTabAdapter(this)
        viewPager.adapter = adapter

        // Attach TabLayout with ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = data[position]
        }.attach()
        val wishlist: ImageButton = view.findViewById(R.id.toolbar_star)
        wishlist.setOnClickListener {
            val intent = Intent(requireActivity(), WishlistActivity::class.java)
            startActivity(intent)
        }
    }
}