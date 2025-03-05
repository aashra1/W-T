package com.example.wt.repository

import android.util.Log
import com.example.wt.model.WishlistModel
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

class ishlistRepositoryImpl : WishlistRepository {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.reference.child("wishlist")

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

    override fun getWishlist(userId: String): Flow<List<WishlistModel>> = callbackFlow {
        val query = ref.orderByChild("userId").equalTo(userId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val wishlist = snapshot.children.mapNotNull { it.getValue(WishlistModel::class.java) }
                trySend(wishlist).isSuccess // Emit data to Flow
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching wishlist: ${error.message}", error.toException())
                close(error.toException()) // Close Flow on error
            }
        }

        query.addValueEventListener(listener)

        awaitClose { query.removeEventListener(listener) } // Remove listener when Flow is closed
    }.catch { e ->
        Log.e("FirebaseError", "Error in wishlist flow: ${e.message}")
        emit(emptyList()) // Emit empty list if there's an error
    }
}