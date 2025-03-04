package com.example.wt.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.wt.R
import com.example.wt.databinding.ActivityUpdateProductBinding
import com.google.android.material.navigation.NavigationView

class UpdateProductActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var binding: ActivityUpdateProductBinding

    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUpdateProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the Navigation Drawer
        drawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navigationView
        navView.setNavigationItemSelectedListener(this)

        // Set up the menu button click listener
        binding.menuImageBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_dashboard -> {
                val intent = Intent(this@UpdateProductActivity, AdminActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_addprod -> {
                val intent = Intent(this@UpdateProductActivity, AddProductActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_updateprod -> {
                val intent = Intent(this@UpdateProductActivity, UpdateProductActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_logout -> {
                val intent = Intent(this@UpdateProductActivity, NavigationActivity::class.java)
                startActivity(intent)
                Toast.makeText(this@UpdateProductActivity,"Logged out from Admin",Toast.LENGTH_SHORT).show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}