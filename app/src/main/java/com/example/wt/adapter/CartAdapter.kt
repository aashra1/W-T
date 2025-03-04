package com.example.wt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wt.R
import com.example.wt.model.CartModel
import com.example.wt.model.ProductModel
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    private val context: Context,
    private val cartItems: ArrayList<CartModel>,
    private val product: Map<String, ProductModel>,
    private val onRemoveClick: (String) -> Unit,
    private val onQuantityChange: (String, Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.cartProductImage)
        val brandName: TextView = itemView.findViewById(R.id.lblbrand)
        val productName: TextView = itemView.findViewById(R.id.cartProductName)
        val productPrice: TextView = itemView.findViewById(R.id.cartProductPrice)
        val productQuantity: TextView = itemView.findViewById(R.id.cartProductQuantity)
        val btnRemove: ImageView = itemView.findViewById(R.id.removeFromCart)
        val btnDecrease: ImageView = itemView.findViewById(R.id.btnDecreaseQuantity)
        val btnIncrease: ImageView = itemView.findViewById(R.id.btnIncreaseQuantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.sample_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        val product = product[cartItem.productId]
        if (product != null) {
            // Set product name
            holder.brandName.text = product.brandName
            holder.productName.text = product.productName

            // Format price with currency symbol
            val formatter = NumberFormat.getCurrencyInstance(Locale.US)
            val totalPrice = cartItem.price * cartItem.quantity
            holder.productPrice.text = formatter.format(totalPrice)

            // Load product image using Glide
            Glide.with(context)
                .load(product.productImage)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(holder.productImage)
        } else {
            holder.productName.text = "Unknown Product"
            holder.productImage.setImageResource(R.drawable.placeholder)
            holder.productPrice.text = "Price Unavailable"
        }

        // Set quantity
        holder.productQuantity.text = cartItem.quantity.toString()

        // Set click listeners
        holder.btnRemove.setOnClickListener {
            cartItem.cartId?.let { onRemoveClick(it) }
        }

        holder.btnDecrease.setOnClickListener {
            val newQuantity = cartItem.quantity - 1
            if (newQuantity >= 1) {
                onQuantityChange(cartItem.cartId, newQuantity)
            } else {
                onRemoveClick(cartItem.cartId) // Auto-remove if quantity reaches 0
            }
        }

        holder.btnIncrease.setOnClickListener {
            onQuantityChange(cartItem.cartId, cartItem.quantity + 1)
        }
    }

    override fun getItemCount(): Int = cartItems.size
}