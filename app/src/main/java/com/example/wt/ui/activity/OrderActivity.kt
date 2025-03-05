package com.example.wt.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wt.R
import com.example.wt.adapter.OrderItemsAdapter
import com.example.wt.databinding.ActivityOrderBinding
import com.example.wt.model.CartModel
import com.example.wt.model.OrderModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderActivity : AppCompatActivity() {

    lateinit var binding: ActivityOrderBinding
    lateinit var orderItemsAdapter: OrderItemsAdapter
    private val orderItems = ArrayList<CartModel>()
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance()

        // Retrieve userId from intent
        val userId = intent.getStringExtra("userId")
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Set up RecyclerView
        binding.orderRecyclerView.layoutManager = LinearLayoutManager(this)
        orderItemsAdapter = OrderItemsAdapter(orderItems)
        binding.orderRecyclerView.adapter = orderItemsAdapter

        // Fetch and display orders
        fetchOrders(userId)

        // Set up back button
        binding.backImageBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun fetchOrders(userId: String) {
        val ordersRef = database.getReference("Orders")

        ordersRef.orderByChild("userId").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    orderItems.clear()
                    var orderFound = false // Flag to check if an order exists

                    for (orderSnapshot in snapshot.children) {
                        val order = orderSnapshot.getValue(OrderModel::class.java)
                        order?.let {
                            orderFound = true
                            // Display order details
                            binding.orderIdTextView.text = "Order ID: ${orderSnapshot.key}"
                            binding.orderStatusTextView.text = "Status: ${order.orderStatus}"
                            binding.orderTotalTextView.text = "Total: Rs. ${order.totalPrice}"

                            // Hide progress bar and show order items
                            binding.progressBarorders.visibility = View.GONE

                            // Add order items to the list
                            order.items?.let { items ->
                                orderItems.addAll(items)
                            }
                        }
                    }

                    if (!orderFound) {
                        Toast.makeText(this@OrderActivity, "No orders found", Toast.LENGTH_SHORT).show()
                        binding.progressBarorders.visibility = View.GONE
                    }

                    orderItemsAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@OrderActivity, "Failed to fetch orders: ${error.message}", Toast.LENGTH_SHORT).show()
                    binding.progressBarorders.visibility = View.GONE
                }
            })
    }

}