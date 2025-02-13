package com.example.wt.viewModel



import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wt.model.ProductModel
import com.example.wt.repository.WishlistRepository

class WishlistViewModel : ViewModel() {
    private val repository = WishlistRepository()

    private val _wishlistItems = MutableLiveData<List<ProductModel>>()
    val wishlistItems: LiveData<List<ProductModel>> get() = _wishlistItems

    // Load wishlist items
    fun loadWishlist() {
        repository.getWishlistItems { items ->
            _wishlistItems.value = items
        }
    }

    // Add item to wishlist
    fun addToWishlist(product: ProductModel) {
        repository.addToWishlist(product) { success ->
            if (success) loadWishlist()
        }
    }

    // Remove item from wishlist
    fun removeFromWishlist(productId: String) {
        repository.removeFromWishlist(productId) { success ->
            if (success) loadWishlist()
        }
    }
}
