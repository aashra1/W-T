package com.example.wt.ui.fragment

import WishlistViewModel
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.wt.R
import com.example.wt.adapter.CustProductsAdapter
import com.example.wt.databinding.FragmentShopBinding
import com.example.wt.model.CartModel
import com.example.wt.model.ProductModel
import com.example.wt.model.WishlistModel
import com.example.wt.repository.ProductRepositoryImpl
import com.example.wt.repository.WishlistRepositoryImpl
import com.example.wt.viewModel.ProductViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ShopFragment : Fragment() {

    private lateinit var binding: FragmentShopBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var productViewModel: ProductViewModel
    private lateinit var wishlistViewModel: WishlistViewModel
    private val productNames = ArrayList<String>() // List to store product names
    private val productList = ArrayList<ProductModel>() // List to store all products
    private lateinit var adapter: CustProductsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShopBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance()

        // Initialize ViewModels
        val productRepo = ProductRepositoryImpl()
        val wishlistRepo = WishlistRepositoryImpl()
        productViewModel = ProductViewModel(productRepo)
        wishlistViewModel = WishlistViewModel(wishlistRepo)

        // Initialize adapter with an empty list
        adapter = CustProductsAdapter(
            context = requireContext(),
            data = ArrayList(),
            wishlistViewModel = wishlistViewModel,
            onAddToWishlistClick = { product -> addToWishlist(product) },
            onAddToCartClick = { product -> addToCart(product) }
        )

        // Set up RecyclerView with GridLayoutManager
        binding.shopRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // 2 columns
        binding.shopRecyclerView.adapter = adapter

        // Fetch product names from the database
        fetchProductNames()

        // Set up AutoCompleteTextView
        setupAutoCompleteTextView()

        // Fetch products from Firebase
        productViewModel.getAllProduct()

        // Observe product data
        productViewModel.allProducts.observe(viewLifecycleOwner) { products ->
            if (products != null && products.isNotEmpty()) {
                productList.clear()
                productList.addAll(products)
                adapter.updateData(products) // Update adapter with new data
            } else {
                // Handle empty or null data
                Toast.makeText(requireContext(), "No products found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchProductNames() {
        val productsRef = database.getReference("products")

        productsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productNames.clear()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(ProductModel::class.java)
                    product?.let {
                        productNames.add(it.productName)
                    }
                }
                updateAutoCompleteAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to fetch products: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupAutoCompleteTextView() {
        val searchBar = binding.searchBar

        // Set up the adapter for AutoCompleteTextView
        val autoCompleteAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, productNames)
        searchBar.setAdapter(autoCompleteAdapter)

        // Handle item selection
        searchBar.setOnItemClickListener { _, _, position, _ ->
            val selectedProductName = productNames[position]
            displayProductDetails(selectedProductName)
        }

        // Add a TextWatcher to detect when the search text is cleared
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Check if the search text is empty
                if (s.isNullOrEmpty()) {
                    // Reset the adapter to show the full list of products
                    adapter.updateData(productList) // Use the correct adapter (CustProductsAdapter)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateAutoCompleteAdapter() {
        val searchBar = binding.searchBar
        val autoCompleteAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, productNames)
        searchBar.setAdapter(autoCompleteAdapter)
    }

    private fun displayProductDetails(productName: String) {
        val selectedProduct = productList.find { it.productName == productName }
        selectedProduct?.let {
            // Clear the current list and add the selected product
            adapter.updateData(listOf(it))
        }
    }

    private fun addToWishlist(product: ProductModel) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val wishlistRef = FirebaseDatabase.getInstance().getReference("Wishlist").child(userId)

        product.productId?.let { productId ->
            wishlistRef.child(productId).get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        Toast.makeText(requireContext(), "Already in Wishlist", Toast.LENGTH_SHORT).show()
                    } else {
                        val wishlistId = System.currentTimeMillis().toString()
                        val newWishlistItem = WishlistModel(
                            wishlistId = wishlistId,
                            productId = productId,
                            brandName = product.brandName ?: "Unknown",
                            productName = product.productName ?: "Unknown",
                            productImage = product.productImage ?: ""
                        )

                        wishlistRef.child(wishlistId).setValue(newWishlistItem)
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(), "Added to Wishlist", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(requireContext(), "Failed to add to Wishlist", Toast.LENGTH_SHORT).show()
                                Log.e("ShopFragment", "Error adding to Wishlist: ${e.message}")
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to check Wishlist", Toast.LENGTH_SHORT).show()
                    Log.e("ShopFragment", "Error checking Wishlist: ${e.message}")
                }
        }
    }

    private fun addToCart(product: ProductModel) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val productId = product.productId
        if (productId == null) {
            Toast.makeText(requireContext(), "Product ID is missing", Toast.LENGTH_SHORT).show()
            return
        }

        val cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(userId)

        cartRef.child(productId).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Update quantity if the item already exists
                val existingCartItem = snapshot.getValue(CartModel::class.java)
                if (existingCartItem != null) {
                    val updatedQuantity = (existingCartItem.quantity ?: 0) + 1
                    cartRef.child(productId).child("quantity").setValue(updatedQuantity)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Cart quantity updated", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Failed to update cart quantity", Toast.LENGTH_SHORT).show()
                            Log.e("ShopFragment", "Error updating cart quantity: ${e.message}")
                        }
                } else {
                    Toast.makeText(requireContext(), "Invalid cart item data", Toast.LENGTH_SHORT).show()
                    Log.e("ShopFragment", "Existing cart item data does not match CartModel")
                }
            } else {
                // Add new item to cart
                val newCartItem = CartModel(
                    userId = userId,
                    cartId = System.currentTimeMillis().toString(),
                    productId = productId,
                    brandName = product.brandName ?: "Unknown",
                    productName = product.productName ?: "Unknown",
                    productImage = product.productImage ?: "",
                    price = product.price ?: 0,
                    quantity = 1
                )
                cartRef.child(productId).setValue(newCartItem)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Added to cart", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Failed to add to cart", Toast.LENGTH_SHORT).show()
                        Log.e("ShopFragment", "Error adding to cart: ${e.message}")
                    }
            }
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Failed to check cart", Toast.LENGTH_SHORT).show()
            Log.e("ShopFragment", "Error checking cart: ${e.message}")
        }
    }
}