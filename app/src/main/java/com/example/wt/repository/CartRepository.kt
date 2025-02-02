package com.example.wt.repository

import com.example.wt.model.CartModel


interface CartRepository {

    fun addToCart(
        cartModel: CartModel,
        callback: (Boolean, String) -> Unit

    )

    fun deleteCart(
        cartId: String,
        callback: (Boolean, String) -> Unit
    )

    fun updateCart(
        cartId: String,
        data: MutableMap<String, Any>,
        callback: (Boolean, String) -> Unit
    )

    fun getCartById(
        cartId: String,
        callback: (CartModel?, Boolean, String) -> Unit
    )

    fun getAllCart(
        callback: (List<CartModel>?, Boolean, String) -> Unit
    )
}