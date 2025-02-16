package com.example.wt.viewmodel

import com.example.wt.model.WishlistModel
import com.example.wt.repository.WishlistRepository
import kotlinx.coroutines.flow.Flow

class WishlistViewModel(val repo: WishlistRepository) {

    fun addToWishlist(wishlistModel: WishlistModel, callback: (Boolean, String) -> Unit) {
        repo.addToWishlist(wishlistModel, callback)
    }

    fun removeFromWishlist(wishlistId: String, callback: (Boolean, String) -> Unit) {
        repo.removeFromWishlist(wishlistId, callback)
    }

    fun getWishlist(userId: String): Flow<List<WishlistModel>> {
        return repo.getWishlist(userId)
    }
}