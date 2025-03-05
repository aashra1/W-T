import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wt.model.WishlistModel
import com.example.wt.repository.WishlistRepository
import android.util.Log

class WishlistViewModel(val repo: WishlistRepository) {

    fun addToWishlist(wishlistModel: WishlistModel, callback: (Boolean, String) -> Unit) {
        Log.d("WishlistRepository", "Adding to wishlist: ${wishlistModel.productName}")
        repo.addToWishlist(wishlistModel, callback)
    }

    fun removeFromWishlist(wishlistId: String, callback: (Boolean, String) -> Unit) {
        repo.removeFromWishlist(wishlistId, callback)
    }

    val _wishlistLiveData = MutableLiveData<List<WishlistModel>>()
    val wishlistLiveData: LiveData<List<WishlistModel>>
        get() = _wishlistLiveData

    var _loading = MutableLiveData<Boolean>()
    var loading = MutableLiveData<Boolean>()
        get() = _loading

    fun getWishlist(userId: String) {
        _loading.value = true
        repo.getWishlist(userId) { success, wishlist, message ->
            if (success) {
                // Use the safe call (?.) to avoid null pointer exceptions
                wishlist?.let {
                    Log.d("WishlistViewModel", "Wishlist fetched successfully, size: ${it.size}")
                    _wishlistLiveData.value = it
                } ?: run {
                    // Log if the wishlist is null
                    Log.e("WishlistViewModel", "Wishlist is null")
                }
            } else {
                // Log if there was an error fetching the wishlist
                Log.e("WishlistViewModel", "Error fetching wishlist: $message")
            }
            _loading.value = false
        }
    }
}
