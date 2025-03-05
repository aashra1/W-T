package com.example.wt.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wt.R
import com.example.wt.adapter.CartAdapter
import com.example.wt.databinding.FragmentCartBinding
import com.example.wt.model.CartModel
import com.example.wt.model.OrderModel
import com.example.wt.model.ProductModel
import com.example.wt.repository.OrderRepositoryImpl
import com.example.wt.ui.activity.OrderActivity
import com.example.wt.viewModel.OrderViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var cartAdapter: CartAdapter
    private val cartItems = ArrayList<CartModel>()
    private val productMap = HashMap<String, ProductModel>()
    private val database = FirebaseDatabase.getInstance().reference
    private lateinit var orderViewModel: OrderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize OrderViewModel
        val orderRepository = OrderRepositoryImpl()
        orderViewModel = OrderViewModel(orderRepository)

        // Set up RecyclerView
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        cartAdapter = CartAdapter(requireContext(), cartItems, productMap,
            onRemoveClick = { cartId -> removeCartItem(cartId) },
            onQuantityChange = { cartId, newQuantity -> updateCartQuantity(cartId, newQuantity) }
        )
        binding.cartRecyclerView.adapter = cartAdapter

        // Load cart items
        fetchCartItems()

        // Set up Secure Checkout button
        binding.secureCheckoutButton.setOnClickListener {
            if (cartItems.isNotEmpty()) {
                // Show confirmation dialog
                showCheckoutConfirmationDialog()
            } else {
                Toast.makeText(requireContext(), "Your cart is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showCheckoutConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Confirm Checkout")
            .setMessage("Are you sure you want to proceed with the checkout?")
            .setPositiveButton("Yes") { dialog, _ ->
                // User confirmed, proceed with checkout
                createOrder()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                // User canceled, dismiss the dialog
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }

    private fun createOrder() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Calculate total price
        val totalPrice = cartItems.sumOf { it.price.toDouble() * it.quantity }

        // Create an order
        val orderId = database.child("Orders").push().key ?: "" // Generate a unique order ID
        val order = OrderModel(
            orderId = orderId,
            userId = userId,
            items = cartItems,
            totalPrice = totalPrice
        )

        orderViewModel.createOrder(order) { success, message ->
            if (success) {
                clearCart(userId)
                Toast.makeText(requireContext(), "Order placed successfully", Toast.LENGTH_SHORT).show()

                // Navigate to OrderActivity with order details
                val intent = Intent(requireContext(), OrderActivity::class.java).apply {
                    putExtra("orderId", orderId)
                    putParcelableArrayListExtra("orderItems", ArrayList(cartItems))
                }
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Failed to place order: $message", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun clearCart(userId: String) {
        val cartRef = database.child("Cart").child(userId)
        cartRef.removeValue()
            .addOnSuccessListener {
                // Clear local cart items
                cartItems.clear()
                updateUI()
            }
            .addOnFailureListener { e ->
                Log.e("CartFragment", "Failed to clear cart: ${e.message}")
            }
    }

    private fun fetchCartItems() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val cartRef = database.child("Cart").child(userId)

        cartRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cartItems.clear()
                for (itemSnapshot in snapshot.children) {
                    try {
                        val cartItem = itemSnapshot.getValue(CartModel::class.java)
                        cartItem?.let {
                            it.cartId = itemSnapshot.key ?: "" // Ensure cartId is set
                            cartItems.add(it)
                        }
                    } catch (e: DatabaseException) {
                        Log.e("CartFragment", "Error parsing cart item: ${itemSnapshot.key}", e)
                        Log.e("CartFragment", "Problematic data: ${itemSnapshot.value}")
                    }
                }

                // Update UI based on cart items
                updateUI()

                // Fetch product details
                fetchProductDetails()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CartFragment", "Database error: ${error.message}")
            }
        })
    }

    private fun updateUI() {
        if (cartItems.isEmpty()) {
            // Hide the Secure Checkout button if the cart is empty
            binding.secureCheckoutButton.visibility = View.GONE
        } else {
            // Show the Secure Checkout button if the cart has items
            binding.secureCheckoutButton.visibility = View.VISIBLE
        }

        // Notify the adapter of data changes
        cartAdapter.notifyDataSetChanged()
    }

    private fun fetchProductDetails() {
        val productRef = database.child("products")

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
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Create an AlertDialog for confirmation
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Remove Item")
            .setMessage("Are you sure you want to remove this item from the cart?")
            .setPositiveButton("Yes") { dialog, _ ->
                // User confirmed, proceed with deletion
                val cartItemRef = database.child("Cart").child(userId).child(cartId)

                // Remove the item from Firebase
                cartItemRef.removeValue()
                    .addOnSuccessListener {
                        // Remove the item from the local list
                        cartItems.removeAll { it.cartId == cartId }
                        // Update UI
                        updateUI()
                        Toast.makeText(requireContext(), "Item removed from cart", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e("CartFragment", "Failed to remove item: ${e.message}")
                        Toast.makeText(requireContext(), "Failed to remove item", Toast.LENGTH_SHORT).show()
                    }
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                // User canceled, dismiss the dialog
                dialog.dismiss()
            }
            .create()

        // Show the dialog
        alertDialog.show()
    }

    private fun updateCartQuantity(cartId: String, newQuantity: Long) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Correct Firebase path: Cart -> userId -> cartId -> quantity
        val quantityRef = database.child("Cart").child(userId).child(cartId).child("quantity")

        // Update the quantity in Firebase
        quantityRef.setValue(newQuantity)
            .addOnSuccessListener {
                Log.d("CartFragment", "Quantity updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("CartFragment", "Failed to update quantity: ${e.message}")
            }
    }
}