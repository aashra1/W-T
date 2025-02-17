package com.example.wt.ui.activity

import WishlistViewModel
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wt.R
import com.example.wt.adapter.WishlistAdapter
import com.example.wt.databinding.ActivityWishlistBinding
import com.example.wt.model.ProductModel
import com.example.wt.model.WishlistModel
import com.example.wt.repository.WishlistRepository
import com.example.wt.repository.WishlistRepositoryImpl
import com.example.wt.ui.fragment.HomeFragment
import com.example.wt.viewModel.WishlistViewModelFactory

class WishlistActivity : AppCompatActivity() {

    lateinit var binding: ActivityWishlistBinding

    lateinit var adapter: WishlistAdapter

    val repo = WishlistRepositoryImpl()
    val wishlistViewModel: WishlistViewModel by viewModels {
        WishlistViewModelFactory(repo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWishlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Navigate to HomeFragment using FragmentTransaction
        binding.backBtn.setOnClickListener {
            val intent = Intent(this@WishlistActivity,NavigationActivity::class.java)
            startActivity(intent)
        }

        adapter = WishlistAdapter(this, ArrayList())
        binding.wishlistRecyclerview.adapter = adapter

        binding.wishlistRecyclerview.layoutManager = LinearLayoutManager(this)

        wishlistViewModel.wishlistLiveData.observe(this, Observer { wishlist ->
            adapter.updateData(wishlist)
        })

        wishlistViewModel.getWishlist("userId")
    }
}