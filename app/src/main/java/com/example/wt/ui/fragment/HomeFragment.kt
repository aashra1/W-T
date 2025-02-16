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
import com.example.wt.viewmodel.WishlistViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {

    private lateinit var wishlistViewModel: WishlistViewModel


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout : TabLayout = view.findViewById(R.id.tabLayout)
        val viewPager: ViewPager2 = view.findViewById(R.id.viewPager)

        val wishlistButton: ImageButton = view.findViewById(R.id.toolbar_star)
        val searchButton: ImageButton = view.findViewById(R.id.toolbar_search)

        // Data for tab titles
        val data = arrayOf("FEATURED", "NEWARRIVALS")

        val adapter = HomeTabAdapter(this)
        viewPager.adapter = adapter

        // Attach TabLayout with ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = data[position]
        }.attach()

        wishlistButton.setOnClickListener {
            val intent = Intent(requireContext(), WishlistActivity::class.java)
            startActivity(intent)
        }

        searchButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(this.id, ShopFragment())
                .addToBackStack(null)
                .commit()
        }

    }


}