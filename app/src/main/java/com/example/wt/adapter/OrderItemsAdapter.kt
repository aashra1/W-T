package com.example.wt.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wt.R
import com.example.wt.model.CartModel
import java.text.NumberFormat
import java.util.Locale

class OrderItemsAdapter(
    private val orderItems: List<CartModel>
) : RecyclerView.Adapter<OrderItemsAdapter.OrderItemViewHolder>() {

    class OrderItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val loading: ProgressBar = itemView.findViewById(R.id.progressBarorder)
        val productImage: ImageView = itemView.findViewById(R.id.orderItemImage)
        val brandName: TextView = itemView.findViewById(R.id.orderItemBrandName)
        val productName: TextView = itemView.findViewById(R.id.orderItemName)
        val productQuantity: TextView = itemView.findViewById(R.id.orderItemQuantity)
        val productPrice: TextView = itemView.findViewById(R.id.orderItemPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val item = orderItems[position]

        holder.loading.visibility = View.GONE

        // Load product image using Glide
        Glide.with(holder.itemView.context)
            .load(item.productImage)
            .placeholder(R.drawable.placeholder) // Default placeholder image
            .error(R.drawable.error) // Default error image
            .into(holder.productImage)

        // Set product details
        holder.brandName.text = item.brandName
        holder.productName.text = item.productName
        holder.productQuantity.text = "Quantity: ${item.quantity}"
        holder.productPrice.text = "Price: Rs. ${item.price * item.quantity}"
    }

    override fun getItemCount(): Int = orderItems.size
}