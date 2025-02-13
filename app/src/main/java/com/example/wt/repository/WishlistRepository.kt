package com.example.wt.repository






import com.example.wt.model.WishlistModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WishlistRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid ?: ""

    fun getWishlist(callback: (List<WishlistModel>) -> Unit) {
        db.collection("users").document(userId).collection("wishlist")
            .get()
            .addOnSuccessListener { result ->
                val wishlist = result.toObjects(WishlistModel::class.java)
                callback(wishlist)
            }
    }

    fun removeFromWishlist(productId: String, callback: (Boolean) -> Unit) {
        db.collection("users").document(userId).collection("wishlist")
            .document(productId)
            .delete()
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }
}

