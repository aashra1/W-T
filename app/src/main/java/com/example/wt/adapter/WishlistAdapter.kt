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
import com.example.wt.R
import com.example.wt.model.WishlistModel
import com.example.wt.viewModel.UserViewModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class WishlistAdapter(
    var context: Context,
    var data: ArrayList<WishlistModel>,
    var wishlistViewModel: WishlistViewModel,
    var userViewModel: UserViewModel
) : RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder>() {

    class WishlistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val loading: ProgressBar = itemView.findViewById(R.id.progressBar4)
        val pImage: ImageView = itemView.findViewById(R.id.wishDisplayImage)
        val bName: TextView = itemView.findViewById(R.id.wishDisplayBrandName)
        val pName: TextView = itemView.findViewById(R.id.wishDisplayProdName)
        val pPrice: TextView = itemView.findViewById(R.id.wishDisplayPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistViewHolder {
        val itemView: View = LayoutInflater.from(context).inflate(R.layout.sample_wishlist, parent, false)
        return WishlistViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WishlistViewHolder, position: Int) {

        val currentUser = userViewModel.getCurrentUser()

        if (currentUser == null) {
            holder.bName.visibility = View.GONE
            holder.pName.visibility = View.GONE
            holder.pPrice.visibility = View.GONE
            holder.pImage.visibility = View.GONE
        } else {
            holder.bName.visibility = View.VISIBLE
            holder.pName.visibility = View.VISIBLE
            holder.pPrice.visibility = View.VISIBLE
            holder.pImage.visibility = View.VISIBLE

            val currentItem = data[position]

            Log.d("WishlistAdapter", "Binding item at position $position: ${currentItem.productName}")

            holder.bName.text = currentItem.brandName
            holder.pName.text = currentItem.productName
            holder.pPrice.text = "Rs. ${currentItem.price}"

            // Load the product image using Picasso
            Picasso.get().load(currentItem.productImage).into(holder.pImage, object : Callback {
                override fun onSuccess() {
                    holder.loading.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    holder.loading.visibility = View.GONE
                }
            })

            // Optionally, allow users to add/remove from wishlist only if logged in
            holder.itemView.setOnClickListener {
                // Add or remove item from wishlist (or trigger an action)
                // For example, adding the item to the user's wishlist
                wishlistViewModel.addToWishlist(currentItem) { success, message ->
                    if (success) {
                        Log.d("WishlistAdapter", "Added to wishlist: ${currentItem.productName}")
                    } else {
                        Log.d("WishlistAdapter", "Error adding to wishlist: $message")
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun updateData(newData: List<WishlistModel>) {
        Log.d("WishlistAdapter", "Updating with data: ${newData.size}")
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }
}