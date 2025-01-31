package com.example.wt.repository


interface CartRepository {

    fun addToCart()

    fun removeAllCart()

    fun removeCartBYId()

    fun updateCart()

    fun getCartById()

    fun getAllCart()
}