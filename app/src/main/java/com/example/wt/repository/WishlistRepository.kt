package com.example.wt.repository

import com.example.wt.model.WishlistModel
import kotlinx.coroutines.flow.Flow

interface WishlistRepository {
    fun addToWishlist(wishlistModel: WishlistModel, callback: (Boolean, String) -> Unit)
    fun removeFromWishlist(wishlistId: String, callback: (Boolean, String) -> Unit)
    fun updateWishlistItem(wishlistId: String, quantity: Int, callback: (Boolean, String) -> Unit)
    fun getWishlist(userId : String, callback: (Boolean, List<WishlistModel>?, String?) -> Unit)
}