package com.example.wt.ui.fragment

import WishlistViewModel
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wt.R
import com.example.wt.adapter.CustProductsAdapter
import com.example.wt.databinding.FragmentNewArrivalsBinding
import com.example.wt.model.CartModel
import com.example.wt.model.ProductModel
import com.example.wt.model.WishlistModel
import com.example.wt.repository.ProductRepositoryImpl
import com.example.wt.repository.WishlistRepositoryImpl
import com.example.wt.viewModel.ProductViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.graphics.Rect // Add this import

class NewArrivalsFragment : Fragment() {

    lateinit var binding: FragmentNewArrivalsBinding
    lateinit var productViewModel: ProductViewModel
    lateinit var wishlistViewModel: WishlistViewModel
    private lateinit var adapter: CustProductsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewArrivalsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize repositories and ViewModels
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
        val gridLayoutManager = GridLayoutManager(requireContext(), 2) // 2 columns
        binding.newRecyclerView.adapter = adapter
        binding.newRecyclerView.layoutManager = gridLayoutManager

        // Fetch products from Firebase
        productViewModel.getAllProduct()

        // Observe product data
        productViewModel.allProducts.observe(viewLifecycleOwner) { products ->
            if (products != null && products.isNotEmpty()) {
                adapter.updateData(products) // Update adapter with new data
            } else {
                // Handle empty or null data
                Toast.makeText(requireContext(), "No products found", Toast.LENGTH_SHORT).show()
            }
        }

        // Observe loading state
        productViewModel.loading.observe(viewLifecycleOwner) { loading ->
            binding.newProgressBar.visibility = if (loading) View.VISIBLE else View.GONE
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
                            brandName = product.brandName?: "Unknown",
                            productName = product.productName ?: "Unknown",
                            productImage = product.productImage ?: ""
                        )

                        wishlistRef.child(wishlistId).setValue(newWishlistItem)
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(), "Added to Wishlist", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(requireContext(), "Failed to add to Wishlist", Toast.LENGTH_SHORT).show()
                                Log.e("NewArrivalsFragment", "Error adding to Wishlist: ${e.message}")
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to check Wishlist", Toast.LENGTH_SHORT).show()
                    Log.e("NewArrivalsFragment", "Error checking Wishlist: ${e.message}")
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
                            Log.e("NewArrivalsFragment", "Error updating cart quantity: ${e.message}")
                        }
                } else {
                    Toast.makeText(requireContext(), "Invalid cart item data", Toast.LENGTH_SHORT).show()
                    Log.e("NewArrivalsFragment", "Existing cart item data does not match CartModel")
                }
            } else {
                // Add new item to cart
                val newCartItem = CartModel(
                    userId = userId,
                    cartId = System.currentTimeMillis().toString(),
                    productId = productId,
                    brandName = product.brandName?: "Unknown",
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
                        Log.e("NewArrivalsFragment", "Error adding to cart: ${e.message}")
                    }
            }
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Failed to check cart", Toast.LENGTH_SHORT).show()
            Log.e("NewArrivalsFragment", "Error checking cart: ${e.message}")
        }
    }
}