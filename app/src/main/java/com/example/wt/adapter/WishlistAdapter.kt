package com.example.wt.adapter

import WishlistViewModel
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wt.R
import com.example.wt.model.ProductModel
import com.example.wt.model.WishlistModel
import com.example.wt.viewModel.UserViewModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class WishlistAdapter(
    var context: Context,
    private val wishlistItems: List<WishlistModel>,
    private val productMap: Map<String, ProductModel>,
    private val onRemoveClick: (String) -> Unit
) : RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder>() {

    class WishlistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val loading: ProgressBar = itemView.findViewById(R.id.progressBar4)
        val productImage: ImageView = itemView.findViewById(R.id.wishlistProductImage)
        val productName: TextView = itemView.findViewById(R.id.wishlistProductName)
        val btnRemove: ImageView = itemView.findViewById(R.id.removeFromWishlist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.sample_wishlist, parent, false)
        return WishlistViewHolder(view)
    }

    override fun onBindViewHolder(holder: WishlistViewHolder, position: Int) {
        val wishlistItem = wishlistItems[position]
        val product = productMap[wishlistItem.productId]

        if (product != null) {
            holder.loading.visibility = View.GONE
            // Set product name
            holder.productName.text = product.productName


            // Load product image using Glide
            Glide.with(context)
                .load(product.productImage)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(holder.productImage)


        } else {
            holder.productName.text = "Unknown Product"
            holder.productImage.setImageResource(R.drawable.placeholder)
            holder.loading.visibility = View.VISIBLE
        }

        // Set click listeners
        holder.btnRemove.setOnClickListener {
            wishlistItem.wishlistId?.let { onRemoveClick(it) }
        }


    }

    override fun getItemCount(): Int = wishlistItems.size

}