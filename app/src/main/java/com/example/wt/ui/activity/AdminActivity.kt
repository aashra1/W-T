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
import androidx.recyclerview.widget.RecyclerView
import com.example.wt.R
import com.example.wt.adapter.ProductsAdapter
import com.example.wt.databinding.ActivityAdminBinding
import com.example.wt.repository.ProductRepositoryImpl
import com.example.wt.viewModel.ProductViewModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.FirebaseDatabase

class AdminActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var binding: ActivityAdminBinding
    lateinit var productViewModel: ProductViewModel
    lateinit var adapter: ProductsAdapter
    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repo = ProductRepositoryImpl()
        productViewModel = ProductViewModel(repo)

        // Set up the Navigation Drawer
        drawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navigationView
        navView.setNavigationItemSelectedListener(this)

        // Set up the menu button click listener
        binding.menuImageBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Initialize the adapter with the onRemoveClick lambda
        adapter = ProductsAdapter(
            context = this@AdminActivity,
            data = ArrayList(),
            onRemoveClick = { productId ->
                // Handle product removal
                removeProduct(productId)
            }
        )

        // Observe product data
        productViewModel.getAllProduct()
        productViewModel.allProducts.observe(this) { products ->
            products?.let {
                adapter.updateData(it)
            }
        }

        // Observe loading state
        productViewModel.loading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        // Set up RecyclerView
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Add spacing between items
        binding.recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.set(5, 5, 5, 5) // Spacing for grid items
            }
        })

        // Set up FloatingActionButton
        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this@AdminActivity, AddProductActivity::class.java)
            startActivity(intent)
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
                Toast.makeText(this@AdminActivity, "Logged out from Admin", Toast.LENGTH_SHORT).show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun removeProduct(productId: String) {
        // Show confirmation dialog
        val alertDialog = android.app.AlertDialog.Builder(this@AdminActivity)
            .setTitle("Remove Product")
            .setMessage("Are you sure you want to remove this product?")
            .setPositiveButton("Yes") { dialog, _ ->
                // User confirmed, proceed with removal
                val productRef = FirebaseDatabase.getInstance().getReference("products").child(productId)
                productRef.removeValue()
                    .addOnSuccessListener {
                        // Product deleted from Firebase, update the local list
                        val position = adapter.getProductPosition(productId)
                        if (position != -1) {
                            adapter.removeProduct(position)
                        }
                        Toast.makeText(this@AdminActivity, "Product removed", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this@AdminActivity, "Failed to remove product: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                // User canceled, dismiss the dialog
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }
}