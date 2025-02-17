package com.example.wt.ui.activity

import WishlistViewModel
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wt.R
import com.example.wt.adapter.WishlistAdapter
import com.example.wt.databinding.ActivityWishlistBinding
import com.example.wt.model.ProductModel
import com.example.wt.model.WishlistModel
import com.example.wt.repository.UserRepositoryImpl
import com.example.wt.repository.WishlistRepository
import com.example.wt.repository.WishlistRepositoryImpl
import com.example.wt.ui.fragment.HomeFragment
import com.example.wt.viewModel.UserViewModel
import com.example.wt.viewModel.WishlistViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class WishlistActivity : AppCompatActivity() {

    lateinit var binding: ActivityWishlistBinding
    lateinit var adapter: WishlistAdapter
    lateinit var wishlistViewModel: WishlistViewModel
    lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWishlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the repository and ViewModels
        val repo = WishlistRepositoryImpl()
        wishlistViewModel = WishlistViewModel(repo)
        userViewModel = UserViewModel(UserRepositoryImpl())

        // Set up the back button to navigate back
        binding.backBtn.setOnClickListener {
            val intent = Intent(this@WishlistActivity, NavigationActivity::class.java)
            startActivity(intent)
        }

        // Initialize the adapter and set it to the RecyclerView
        adapter = WishlistAdapter(this, ArrayList(), wishlistViewModel, userViewModel)
        binding.wishlistRecyclerview.adapter = adapter
        binding.wishlistRecyclerview.layoutManager = LinearLayoutManager(this)

        // Observe the loading state to show/hide the ProgressBar
        wishlistViewModel.loading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        wishlistViewModel.wishlistLiveData.observe(this) { wishlist ->
            Log.d("WishlistActivity", "Wishlist data observed: ${wishlist.size}")
            wishlist?.let {
                if (it.isEmpty()) {
                    Log.d("WishlistActivity", "Wishlist is empty")
                } else {
                    Log.d("WishlistActivity", "Wishlist size: ${it.size}")
                    adapter.updateData(it)
                }
            }
        }

        // Get userId from Firebase Auth and fetch the wishlist
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        Log.d("WishlistActivity", "User ID: $userId")
        if (!userId.isNullOrEmpty()) {
            wishlistViewModel.getWishlist(userId)
        } else {
            Snackbar.make(binding.root, "Unable to Login", Snackbar.LENGTH_LONG)
                .setAction("Ok") {}.show()
        }
    }
}
