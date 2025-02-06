package com.example.wt.repository

import com.example.wt.model.CartModel
import com.google.firebase.database.*

class CartRepositoryImpl : CartRepository {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val cartRef: DatabaseReference = database.reference.child("carts")

    override fun addToCart(cartModel: CartModel, callback: (Boolean, String) -> Unit) {
        val cartId = cartRef.push().key ?: return callback(false, "Failed to generate cart ID")
        cartModel.cartId = cartId

        cartRef.child(cartId).setValue(cartModel).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true, "Item added to cart successfully")
            } else {
                callback(false, task.exception?.message ?: "Error adding item to cart")
            }
        }
    }

    override fun deleteCart(cartId: String, callback: (Boolean, String) -> Unit) {
        cartRef.child(cartId).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true, "Item removed from cart successfully")
            } else {
                callback(false, task.exception?.message ?: "Error removing item from cart")
            }
        }
    }

    override fun deleteProductInCart(
        cartId: String,
        productId: String,
        callback: (Boolean, String) -> Unit
    ) {
        cartRef.child(cartId).removeValue().addOnCompleteListener {
            if(it.isSuccessful){
                callback(true,"Product Removed from Cart successfully")
            } else{
                callback(false,it.exception?.message?:"Unknown error")
            }
        }
    }

    override fun updateCart(
        cartId: String,
        data: MutableMap<String, Any>,
        callback: (Boolean, String) -> Unit
    ) {
        cartRef.child(cartId).updateChildren(data).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true, "Cart updated successfully")
            } else {
                callback(false, task.exception?.message ?: "Error updating cart")
            }
        }
    }


    override fun getCartById(cartId: String, callback: (CartModel?, Boolean, String) -> Unit) {
        cartRef.child(cartId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val cartModel = snapshot.getValue(CartModel::class.java)
                    callback(cartModel, true, "Cart item fetched successfully")
                } else {
                    callback(null, false, "Cart item not found")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, false, error.message)
            }
        })
    }

    override fun getAllCart(callback: (List<CartModel>?, Boolean, String) -> Unit) {
        cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cartList = mutableListOf<CartModel>()
                if (snapshot.exists()) {
                    for (cartSnapshot in snapshot.children) {
                        val cartItem = cartSnapshot.getValue(CartModel::class.java)
                        if (cartItem != null) {
                            cartList.add(cartItem)
                        }
                    }
                    callback(cartList, true, "All cart items fetched successfully")
                } else {
                    callback(emptyList(), false, "No items found in the cart")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, false, error.message)
            }
        })
    }
}


