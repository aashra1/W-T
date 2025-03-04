package com.example.wt.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wt.R
import com.example.wt.adapter.CartAdapter
import com.example.wt.databinding.FragmentCartBinding
import com.example.wt.model.CartModel
import com.example.wt.model.ProductModel
import com.google.firebase.database.*

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var cartAdapter: CartAdapter
    private val cartItems = ArrayList<CartModel>()
    private val productMap = HashMap<String, ProductModel>()
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        cartAdapter = CartAdapter(requireContext(), cartItems, productMap,
            onRemoveClick = { cartId -> removeCartItem(cartId) },
            onQuantityChange = { cartId, newQuantity -> updateCartQuantity(cartId, newQuantity) }
        )
        binding.cartRecyclerView.adapter = cartAdapter

        // Load cart items
        fetchCartItems()
    }

    private fun fetchCartItems() {
        val cartRef = database.child("Cart")

        cartRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cartItems.clear()
                for (itemSnapshot in snapshot.children) {
                    val cartItem = itemSnapshot.getValue(CartModel::class.java)
                    cartItem?.let { cartItems.add(it) }
                }
                fetchProductDetails()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun fetchProductDetails() {
        val productRef = database.child("Products")

        productRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productMap.clear()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(ProductModel::class.java)
                    product?.let { productMap[product.productId] = it }
                }
                cartAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun removeCartItem(cartId: String) {
        database.child("Cart").child(cartId).removeValue().addOnSuccessListener {
            cartItems.removeAll { it.cartId == cartId }
            cartAdapter.notifyDataSetChanged()
        }
    }

    private fun updateCartQuantity(cartId: String, newQuantity: Int) {
        database.child("Cart").child(cartId).child("quantity").setValue(newQuantity)
    }
}
