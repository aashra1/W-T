package com.example.wt.viewModel







import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wt.model.WishlistModel
import com.example.wt.repository.WishlistRepository

class WishlistViewModel : ViewModel() {

    private val repository = WishlistRepository()

    private val _wishlist = MutableLiveData<List<WishlistModel>>()
    val wishlist: LiveData<List<WishlistModel>> get() = _wishlist

    private val _operationStatus = MutableLiveData<Boolean>()
    val operationStatus: LiveData<Boolean> get() = _operationStatus

    init {
        fetchWishlist()
    }

    fun fetchWishlist() {
        repository.getWishlist { items ->
            _wishlist.value = items
        }
    }

    fun removeFromWishlist(productId: String) {
        repository.removeFromWishlist(productId) { success ->
            _operationStatus.value = success
            if (success) {
                fetchWishlist()
            }
        }
    }
}
