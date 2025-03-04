package com.example.wt.ui.activity

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wt.R
import com.example.wt.adapter.ProductsAdapter
import com.example.wt.databinding.ActivityAdminBinding
import com.example.wt.repository.ProductRepositoryImpl
import com.example.wt.viewModel.ProductViewModel
import com.google.android.material.navigation.NavigationView

class AdminActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {

    lateinit var binding : ActivityAdminBinding

    lateinit var productViewModel: ProductViewModel

    lateinit var adapter: ProductsAdapter

    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var repo = ProductRepositoryImpl()
        productViewModel = ProductViewModel(repo)

        // Set up the Navigation Drawer
        drawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navigationView
        navView.setNavigationItemSelectedListener(this)

        // Set up the menu button click listener
        binding.menuImageBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        adapter = ProductsAdapter(this@AdminActivity,
            ArrayList())

        productViewModel.getAllProduct()

        productViewModel.allProducts.observe(this){it->
            it?.let {
                adapter.updateData(it)
            }
        }

        productViewModel.loading.observe(this){loading->
            if(loading){ // true
                binding.progressBar.visibility = View.VISIBLE
            }else{
                binding.progressBar.visibility = View.GONE

            }
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)

        binding.floatingActionButton.setOnClickListener {
            var intent = Intent(this@AdminActivity, AddProductActivity::class.java)
            startActivity(intent)
        }

        // Add spacing between items
        binding.recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.set(5, 5, 5, 5) // Spacing for grid items
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_dashboard -> {
                val intent = Intent(this@AdminActivity, AdminActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_addprod -> {
                val intent = Intent(this@AdminActivity, AddProductActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_updateprod -> {
                val intent = Intent(this@AdminActivity, UpdateProductActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_logout -> {
                val intent = Intent(this@AdminActivity, NavigationActivity::class.java)
                startActivity(intent)
                Toast.makeText(this@AdminActivity,"Logged out from Admin", Toast.LENGTH_SHORT).show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}