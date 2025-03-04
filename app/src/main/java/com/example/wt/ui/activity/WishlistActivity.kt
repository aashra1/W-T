package com.example.wt.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.wt.adapter.WishlistAdapter
import com.example.wt.databinding.ActivityWishlistBinding
import com.example.wt.model.ProductModel
import com.example.wt.model.WishlistModel
import com.example.wt.repository.ProductRepositoryImpl
import com.example.wt.repository.WishlistRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList
import java.util.HashMap

class WishlistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWishlistBinding
    private lateinit var wishlistAdapter: WishlistAdapter
    private lateinit var wishlistRepository: WishlistRepositoryImpl
    private lateinit var productRepository: ProductRepositoryImpl

    private var wishlistList = ArrayList<WishlistModel>()
    private var productMap = HashMap<String, ProductModel>() // Maps productId to product details

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWishlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        wishlistRepository = WishlistRepositoryImpl()
        productRepository = ProductRepositoryImpl()

        setupRecyclerView()
        fetchWishlistItems()

        // Set up the back button to navigate back
        binding.backBtn.setOnClickListener {
            val intent = Intent(this@WishlistActivity, NavigationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        wishlistAdapter = WishlistAdapter(this@WishlistActivity, wishlistList, productMap,
            onRemoveClick = { wishlistId -> removeFromWishlist(wishlistId) }
        )

        val layoutManager = GridLayoutManager(this, 1)
        layoutManager.isSmoothScrollbarEnabled = true

        binding.wishlistRecyclerview.apply {
            this.layoutManager = layoutManager
            this.adapter = wishlistAdapter
            this.setHasFixedSize(true)
            this.itemAnimator = null
        }
    }

    private fun fetchWishlistItems() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val wishlistRef = FirebaseDatabase.getInstance().getReference("Wishlist").child(userId)

        wishlistRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                wishlistList.clear()
                for (itemSnapshot in snapshot.children) {
                    val wishlistItem = itemSnapshot.getValue(WishlistModel::class.java)
                    wishlistItem?.let { wishlistList.add(it) }
                }
                wishlistAdapter.notifyDataSetChanged()
                fetchProductDetails()
                binding.progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@WishlistActivity, "Failed to load wishlist items", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchProductDetails() {
        val productIds = wishlistList.map { it.productId }.toSet()

        productIds.forEach { productId ->
            productRepository.getProductById(productId) { product, success, _ ->
                if (success && product != null) {
                    productMap[productId] = product
                    wishlistAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun removeFromWishlist(wishlistId: String) {
        wishlistRepository.removeFromWishlist(wishlistId) { success, _ ->
            if (success) {
                Toast.makeText(this@WishlistActivity,"Removed from Wishlisht",Toast.LENGTH_SHORT).show()
            }
        }
    }
}