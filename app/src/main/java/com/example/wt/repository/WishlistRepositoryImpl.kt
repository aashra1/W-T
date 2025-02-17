package com.example.wt.repository

import android.util.Log
import com.example.wt.model.WishlistModel
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

class WishlistRepositoryImpl : WishlistRepository {

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref: DatabaseReference = database.reference.child("wishlist")

    override fun addToWishlist(wishlistModel: WishlistModel, callback: (Boolean, String) -> Unit) {
        val wishlistId = ref.push().key ?: return callback(false, "Failed to generate ID")
        wishlistModel.wishlistId = wishlistId
        ref.child(wishlistId).setValue(wishlistModel)
            .addOnSuccessListener { callback(true, "Added to wishlist") }
            .addOnFailureListener { callback(false, it.message ?: "Error adding to wishlist") }
    }

    override fun removeFromWishlist(wishlistId: String, callback: (Boolean, String) -> Unit) {
        ref.child(wishlistId).removeValue()
            .addOnSuccessListener { callback(true, "Removed from wishlist") }
            .addOnFailureListener { callback(false, it.message ?: "Error removing from wishlist") }
    }

    override fun getWishlist(userId : String, callback: (Boolean, List<WishlistModel>?, String?) -> Unit) {
        val query = ref.orderByChild("userId").equalTo(userId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val wishlist = snapshot.children.mapNotNull { it.getValue(WishlistModel::class.java) }
                callback(true, wishlist, null)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, null, error.message)
            }
        })
    }

}