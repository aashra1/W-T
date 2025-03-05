package com.example.wt.repository

import com.example.wt.model.CartModel
import com.google.firebase.database.*

class CartRepositoryImpl : CartRepository {

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref: DatabaseReference = database.reference.child("carts")

    override fun addToCart(cartModel: CartModel, callback: (Boolean, String) -> Unit) {
        val cartId = ref.push().key ?: return callback(false, "Failed to generate cart ID")
        cartModel.cartId = cartId

        ref.child(cartId).setValue(cartModel).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Added to cart")
            } else {
                callback(false, it.exception?.message.toString())
            }
        }
    }

    override fun removeFromCart(cartId: String, callback: (Boolean, String) -> Unit) {
        ref.child(cartId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Removed from cart")
            } else {
                callback(false, it.exception?.message.toString())
            }
        }
    }

    override fun updateCartItem(cartId: String, quantity: Int, callback: (Boolean, String) -> Unit) {
        ref.child(cartId).child("quantity").setValue(quantity).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Cart updated")
            } else {
                callback(false, it.exception?.message.toString())
            }
        }
    }

    override fun getCartItems(userId: String, callback: (List<CartModel>?, Boolean, String) -> Unit) {
        ref.orderByChild("userId").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val cartItems = mutableListOf<CartModel>()
                    for (item in snapshot.children) {
                        val cartItem = item.getValue(CartModel::class.java)
                        if (cartItem != null) {
                            cartItems.add(cartItem)
                        }
                    }
                    callback(cartItems, true, "Cart items fetched")
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null, false, error.message)
                }
            })
    }
}