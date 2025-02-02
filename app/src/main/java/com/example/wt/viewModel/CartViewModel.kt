package com.example.wt.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wt.model.CartModel
import com.example.wt.repository.CartRepository

class CartViewModel(private val repo: CartRepository) : ViewModel() {


    private val _cart = MutableLiveData<CartModel>()
    val cart: MutableLiveData<CartModel> get() = _cart


    private val _allCartItems = MutableLiveData<List<CartModel>>()
    val allCartItems: MutableLiveData<List<CartModel>> get() = _allCartItems


    fun addToCart(cartModel: CartModel, callback: (Boolean, String) -> Unit) {
        repo.addToCart(cartModel) { success, message ->
            callback(success, message)
        }
    }


    fun deleteCart(cartId: String, callback: (Boolean, String) -> Unit) {
        repo.deleteCart(cartId) { success, message ->
            callback(success, message)
        }
    }


    fun updateCart(cartId: String, data: MutableMap<String, Any>, callback: (Boolean, String) -> Unit) {
        repo.updateCart(cartId, data) { success, message ->
            callback(success, message)
        }
    }


    fun getCartById(cartId: String) {
        repo.getCartById(cartId) { cartItem, success, message ->
            if (success) {
                _cart.value = cartItem
            } else {
                // Handle the error case if needed
                _cart.value = null
            }
        }
    }


    fun getAllCart() {
        repo.getAllCart { cartList, success, message ->
            if (success) {
                _allCartItems.value = cartList
            } else {
                // Handle the error case if needed
                _allCartItems.value = emptyList()
            }
        }
    }
}



