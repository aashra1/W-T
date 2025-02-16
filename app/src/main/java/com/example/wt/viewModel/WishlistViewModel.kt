import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wt.model.ProductModel
import com.example.wt.model.WishlistModel
import com.example.wt.repository.WishlistRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class WishlistViewModel(val repo: WishlistRepository) : ViewModel() {

    fun addToWishlist(wishlistModel: WishlistModel, callback: (Boolean, String) -> Unit) {
        repo.addToWishlist(wishlistModel, callback)
    }

    fun removeFromWishlist(wishlistId: String, callback: (Boolean, String) -> Unit) {
        repo.removeFromWishlist(wishlistId, callback)
    }

    val _wishlistLiveData = MutableLiveData<List<WishlistModel>>() // Change type
    val wishlistLiveData: LiveData<List<WishlistModel>>
        get() = _wishlistLiveData

    fun getWishlist(userId: String) {
        viewModelScope.launch {
            repo.getWishlist(userId).collect { wishlist ->
                _wishlistLiveData.postValue(wishlist) // Directly update with WishlistModel
            }
        }
    }

}
