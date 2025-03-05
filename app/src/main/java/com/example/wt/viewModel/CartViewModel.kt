package com.example.wt.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wt.model.CartModel
import com.example.wt.repository.CartRepository

class CartViewModel(private val repo: CartRepository) : ViewModel() {


    private val _cartItems = MutableLiveData<List<CartModel>?>()
    val cartItems: LiveData<List<CartModel>?> get() = _cartItems

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun addToCart(cartModel: CartModel) {
        _loading.value = true
        repo.addToCart(cartModel) { success, message ->
            _loading.value = false
            if (success) {
                getCartItems(cartModel.userId)
            } else {
                _errorMessage.value = message
            }
        }
    }

    fun removeFromCart(cartId: String, userId: String) {
        _loading.value = true
        repo.removeFromCart(cartId) { success, message ->
            _loading.value = false
            if (success) {
                getCartItems(userId)
            } else {
                _errorMessage.value = message
            }
        }
    }

    fun updateCartItem(cartId: String, quantity: Int, userId: String) {
        _loading.value = true
        repo.updateCartItem(cartId, quantity) { success, message ->
            _loading.value = false
            if (success) {
                getCartItems(userId)
            } else {
                _errorMessage.value = message
            }
        }
    }

    fun getCartItems(userId: String) {
        _loading.value = true
        repo.getCartItems(userId) { cartItems, success, message ->
            _loading.value = false
            if (success) {
                _cartItems.value = cartItems
            } else {
                _errorMessage.value = message
            }
        }
    }
}



