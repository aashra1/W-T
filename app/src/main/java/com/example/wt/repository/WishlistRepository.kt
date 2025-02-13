package com.example.wt.repository



import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.wt.model.ProductModel

class WishlistRepository {
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    // Add item to wishlist
    fun addToWishlist(product: ProductModel, onComplete: (Boolean) -> Unit) {
        userId?.let {
            db.collection("users").document(it).collection("wishlist")
                .document(product.productId)
                .set(product)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        }
    }

    // Remove item from wishlist
    fun removeFromWishlist(productId: String, onComplete: (Boolean) -> Unit) {
        userId?.let {
            db.collection("users").document(it).collection("wishlist")
                .document(productId)
                .delete()
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        }
    }

    // Fetch all wishlist items
    fun getWishlistItems(onResult: (List<ProductModel>) -> Unit) {
        if (userId != null) {
            db.collection("users").document(userId).collection("wishlist")
                .get()
                .addOnSuccessListener { snapshot ->
                    val items = snapshot.toObjects(ProductModel::class.java)
                    onResult(items)
                }
                .addOnFailureListener { onResult(emptyList()) }
        } else {
            onResult(emptyList())
        }
    }

    // Check if a product is in wishlist
    fun isProductInWishlist(productId: String, onResult: (Boolean) -> Unit) {
        userId?.let {
            db.collection("users").document(it).collection("wishlist")
                .document(productId)
                .get()
                .addOnSuccessListener { document ->
                    onResult(document.exists())
                }
                .addOnFailureListener { onResult(false) }
        }
    }
}
