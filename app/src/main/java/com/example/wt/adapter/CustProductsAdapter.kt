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
import com.example.wt.model.ProductModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class CustProductsAdapter(
    var context: Context,
    var data: ArrayList<ProductModel>,
    var wishlistViewModel: WishlistViewModel,
    private val onAddToWishlistClick: (ProductModel) -> Unit,
    private val onAddToCartClick: (ProductModel) -> Unit
) : RecyclerView.Adapter<CustProductsAdapter.CustProductViewHolder>() {

    class CustProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val loading: ProgressBar = itemView.findViewById(R.id.progressBar3)
        val pImage: ImageView = itemView.findViewById(R.id.newDisplayImage)
        val bName: TextView = itemView.findViewById(R.id.newDisplayBrandName)
        val wish: ImageView = itemView.findViewById(R.id.WishImage)
        val wished: ImageView = itemView.findViewById(R.id.WishedImage1)
        val cart: ImageView = itemView.findViewById(R.id.addToCart)
        val pName: TextView = itemView.findViewById(R.id.newDisplayProdName)
        val pPrice: TextView = itemView.findViewById(R.id.newDisplayPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustProductViewHolder {
        val itemView: View = LayoutInflater.from(context).inflate(R.layout.sample_viewprod, parent, false)
        return CustProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CustProductViewHolder, position: Int) {
        val product = data[position]

        // Bind data to views
        holder.bName.text = product.brandName
        holder.pName.text = product.productName
        holder.pPrice.text = "Rs. ${product.price}"

        // Load product image using Picasso
        Picasso.get().load(product.productImage).into(holder.pImage, object : Callback {
            override fun onSuccess() {
                holder.loading.visibility = View.GONE
            }

            override fun onError(e: Exception?) {
                holder.loading.visibility = View.GONE
            }
        })

        // Handle wishlist button clicks
        holder.wish.setOnClickListener {
            Log.d("WishlistDebug", "Add to Wishlist click for product: ${product.productName}")
            onAddToWishlistClick(product)
            holder.wish.visibility = View.GONE
            holder.wished.visibility = View.VISIBLE
        }

        // Handle cart button clicks
        holder.cart.setOnClickListener {
            Log.d("CartDebug", "Add to Cart clicked for product: ${product.productName}")
            onAddToCartClick(product)
        }

        // Handle removing from wishlist
        holder.wished.setOnClickListener {
            wishlistViewModel.removeFromWishlist(product.productId) { success, message ->
                if (success) {
                    holder.wish.visibility = View.VISIBLE
                    holder.wished.visibility = View.GONE
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    // Method to update data in the adapter
    fun updateData(products: List<ProductModel>) {
        data.clear()
        data.addAll(products)
        notifyDataSetChanged()
    }
}