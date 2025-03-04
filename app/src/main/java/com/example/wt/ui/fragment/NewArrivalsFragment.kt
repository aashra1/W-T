package com.example.wt.ui.fragment

import WishlistViewModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wt.R
import com.example.wt.adapter.CustProductsAdapter
import com.example.wt.adapter.ProductsAdapter
import com.example.wt.databinding.FragmentNewArrivalsBinding
import com.example.wt.model.CartModel
import com.example.wt.model.ProductModel
import com.example.wt.model.WishlistModel
import com.example.wt.repository.ProductRepositoryImpl
import com.example.wt.repository.WishlistRepositoryImpl
import com.example.wt.viewModel.ProductViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class NewArrivalsFragment : Fragment() {

    lateinit var binding: FragmentNewArrivalsBinding
    lateinit var productViewModel: ProductViewModel
    lateinit var wishlistViewModel: WishlistViewModel
    lateinit var adapter: CustProductsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewArrivalsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productRepo = ProductRepositoryImpl()
        val wishlistRepo = WishlistRepositoryImpl()

        productViewModel = ProductViewModel(productRepo)
        wishlistViewModel = WishlistViewModel(wishlistRepo)

        adapter = CustProductsAdapter(requireContext(), ArrayList(), wishlistViewModel, { product -> addToWishlist(product) }, { product -> addToCart(product) })

        productViewModel.getAllProduct()

        // Use viewLifecycleOwner for observing LiveData in fragments
        productViewModel.allProducts.observe(viewLifecycleOwner) { products ->
            products?.let {
                adapter.updateData(it)
            }
        }

        productViewModel.loading.observe(viewLifecycleOwner) { loading ->
            binding.newProgressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        binding.newRecyclerView.adapter = adapter
        binding.newRecyclerView.layoutManager = LinearLayoutManager(requireContext())
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
                        println("DEBUG: Item already exists in Wishlist")
                    } else {
                        val wishlistId = System.currentTimeMillis().toString()
                        val newWishlistItem = WishlistModel(
                            wishlistId = wishlistId,
                            productId = productId,
                            productName = product.productName ?: "Unknown",
                            productImage = product.productImage ?: "",
                        )

                        wishlistRef.child(wishlistId).setValue(newWishlistItem)
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(), "Added to Wishlist", Toast.LENGTH_SHORT).show()
                                println("DEBUG: Successfully added to Wishlist")
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(requireContext(), "Failed to add to Wishlist", Toast.LENGTH_SHORT).show()
                                println("DEBUG: Error adding to Wishlist: ${e.message}")
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to check Wishlist", Toast.LENGTH_SHORT).show()
                    println("DEBUG: Error checking Wishlist: ${e.message}")
                }
        }
    }

    private fun addToCart(product: ProductModel) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(userId ?: return)

        product.productId?.let { productId ->  // Ensure productId is non-null
            cartRef.child(productId).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val existingCartItem = snapshot.getValue(CartModel::class.java)
                    val updatedQuantity = (existingCartItem?.quantity ?: 0) + 1
                    cartRef.child(productId).child("quantity").setValue(updatedQuantity)
                } else {
                    val newCartItem = CartModel(
                        userId = userId,
                        cartId = System.currentTimeMillis().toString(),
                        productId = productId,
                        productName = product.productName ?: "Unknown",
                        productImage = product.productImage ?: "",
                        price = product.price ?: 0,
                        quantity = 1
                    )
                    cartRef.child(productId).setValue(newCartItem)
                }
                Toast.makeText(requireContext(), "Added to cart", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to add to cart", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
