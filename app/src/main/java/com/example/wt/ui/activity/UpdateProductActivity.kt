package com.example.wt.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.wt.R
import com.example.wt.databinding.ActivityUpdateProductBinding
import com.example.wt.model.ProductModel
import com.example.wt.repository.ProductRepositoryImpl
import com.example.wt.viewModel.ProductViewModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class UpdateProductActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var binding: ActivityUpdateProductBinding
    lateinit var drawerLayout: DrawerLayout
    private lateinit var productViewModel: ProductViewModel
    private val productList = mutableListOf<ProductModel>() // List to store all products
    private val productNameList = mutableListOf<String>() // List to store product names for the spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUpdateProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        productViewModel = ProductViewModel(ProductRepositoryImpl())

        // Set up the Navigation Drawer
        drawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navigationView
        navView.setNavigationItemSelectedListener(this)

        // Set up the menu button click listener
        binding.menuImageBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Fetch products from Firestore and populate the spinner
        fetchProducts()

        // Set up the search spinner
        setupSpinnerSearch()

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Add click listener to the Update Product button
        binding.updateProductButton.setOnClickListener {
            updateProduct()
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
                Toast.makeText(this@UpdateProductActivity, "Logged out from Admin", Toast.LENGTH_SHORT).show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun fetchProducts() {
        val db = Firebase.firestore
        db.collection("products").get().addOnSuccessListener { documents ->
            if (documents.isEmpty) {
                Toast.makeText(this, "No products found in Firestore", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }

            productList.clear()
            productNameList.clear()

            // Add a default empty value
            productNameList.add("Select a product")

            // Add all product names
            for (document in documents) {
                val product = document.toObject(ProductModel::class.java)
                productList.add(product)
                productNameList.add(product.productName ?: "")
            }

            // Update the search spinner adapter
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, productNameList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.productSpinner.adapter = adapter

            // Set the default selection to the empty value
            binding.productSpinner.setSelection(0)
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error loading products: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("UpdateProductActivity", "Firestore query failed: ${e.message}")
        }
    }

    private fun setupSpinnerSearch() {
        val spinnerSearch = binding.productSpinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, productNameList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSearch.adapter = adapter

        spinnerSearch.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    // "Select a product" is selected
                    binding.layoutProductDetails.visibility = View.GONE
                    clearProductDetails()
                } else {
                    // A product is selected
                    binding.layoutProductDetails.visibility = View.VISIBLE
                    val selectedProductName = productNameList[position]
                    val selectedProduct = productList.find { it.productName == selectedProductName }
                    selectedProduct?.let { displayProductDetails(it) }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Clear the product details if nothing is selected
                binding.layoutProductDetails.visibility = View.GONE
                clearProductDetails()
            }
        }
    }

    private fun displayProductDetails(product: ProductModel) {
        Log.d("UpdateProductActivity", "Product Details: $product")

        // Update the UI with the product details
        binding.brandName.setText(product.brandName)
        binding.productName.setText(product.productName)
        binding.price.setText(product.price.toString())
        Glide.with(this).load(product.productImage).into(binding.productImage)
    }

    private fun clearProductDetails() {
        // Clear the UI fields
        binding.brandName.text.clear()
        binding.productName.text.clear()
        binding.price.text.clear()
        binding.productImage.setImageResource(R.drawable.placeholder)
    }

    private fun updateProduct() {
        // Get the selected product from the search spinner
        val selectedProductName = binding.productSpinner.selectedItem as? String
        if (selectedProductName == "Select a product" || selectedProductName.isNullOrEmpty()) {
            // Show an error message if no product is selected
            Log.e("UpdateProductActivity", "No product selected")
            Toast.makeText(this@UpdateProductActivity, "No product selected", Toast.LENGTH_SHORT).show()
            return
        }

        // Find the selected product in the productList
        val selectedProduct = productList.find { it.productName == selectedProductName }
        if (selectedProduct == null) {
            Log.e("UpdateProductActivity", "Selected product not found")
            Toast.makeText(this@UpdateProductActivity, "Selected product not found", Toast.LENGTH_SHORT).show()
            return
        }

        // Collect updated data from the UI fields
        val updatedData = mutableMapOf<String, Any>()

        // Brand Name
        val updatedBrandName = binding.brandName.text.toString()
        if (updatedBrandName.isNotEmpty()) {
            updatedData["brandName"] = updatedBrandName
        }

        // Product Name
        val updatedProductName = binding.productName.text.toString()
        if (updatedProductName.isNotEmpty()) {
            updatedData["productName"] = updatedProductName
        }

        // Price
        val updatedPrice = binding.price.text.toString().toDoubleOrNull()
        if (updatedPrice != null) {
            updatedData["price"] = updatedPrice
        }

        // Image URL (if updated)
        val updatedImageUrl = binding.productImage.tag as? String
        if (!updatedImageUrl.isNullOrEmpty()) {
            updatedData["productImage"] = updatedImageUrl
        }

        // Call the ViewModel to update the product
        productViewModel.updateProduct(selectedProduct.productId, updatedData) { success, message ->
            if (success) {
                Log.d("UpdateProductActivity", "Product updated successfully: $message")
                Toast.makeText(this@UpdateProductActivity, message, Toast.LENGTH_SHORT).show()
                clearProductDetails()
            } else {
                Log.e("UpdateProductActivity", "Failed to update product: $message")
                Toast.makeText(this@UpdateProductActivity, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}