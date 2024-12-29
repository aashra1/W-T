package com.example.wt.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.wt.R
import com.example.wt.databinding.ActivityNavigationBinding
import com.example.wt.ui.fragment.AccountFragment
import com.example.wt.ui.fragment.CartFragment
import com.example.wt.ui.fragment.HomeFragment
import com.example.wt.ui.fragment.ShopFragment

class NavigationActivity : AppCompatActivity() {
    lateinit var binding: ActivityNavigationBinding
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager : FragmentManager = supportFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(binding.frameNavigation.id,fragment)
        fragmentTransaction.commit()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(HomeFragment())

        binding.bottomNavigationView.setOnItemSelectedListener { menu ->
            when(menu.itemId){
                R.id.navHome -> replaceFragment(HomeFragment())
                R.id.navShop ->replaceFragment(ShopFragment())
                R.id.navCart ->replaceFragment(CartFragment())
                R.id.navAccount ->replaceFragment(AccountFragment())
                else -> {}

            }
            true

        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }
}