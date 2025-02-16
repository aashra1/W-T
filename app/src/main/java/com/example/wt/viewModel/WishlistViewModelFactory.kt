package com.example.wt.viewModel

import WishlistViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wt.repository.WishlistRepository

class WishlistViewModelFactory(val repo: WishlistRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WishlistViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WishlistViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
