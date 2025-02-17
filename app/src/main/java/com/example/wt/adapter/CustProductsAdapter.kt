package com.example.wt.adapter

import WishlistViewModel
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wt.R
import com.example.wt.adapter.ProductsAdapter.ProductViewHolder
import com.example.wt.model.ProductModel
import com.example.wt.model.WishlistModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class CustProductsAdapter(
    var context: Context,
    var data: ArrayList<ProductModel>,
    var wishlistViewModel: WishlistViewModel
) : RecyclerView.Adapter<CustProductsAdapter.CustProductViewHolder>() {

    class CustProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val loading: ProgressBar = itemView.findViewById(R.id.progressBar3)
        val pImage: ImageView = itemView.findViewById(R.id.newDisplayImage)
        val bName: TextView = itemView.findViewById(R.id.newDisplayBrandName)
        val wish: ImageView = itemView.findViewById(R.id.WishImage)
        val wished: ImageView = itemView.findViewById(R.id.WishedImage1)
        val pName: TextView = itemView.findViewById(R.id.newDisplayProdName)
        val pPrice: TextView = itemView.findViewById(R.id.newDisplayPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustProductViewHolder {
        val itemView: View = LayoutInflater.from(context).inflate(R.layout.sample_viewprod, parent, false)
        return CustProductViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: CustProductViewHolder, position: Int) {
        val product = data[position]

        holder.bName.text = product.brandName
        holder.pName.text = product.productName
        holder.pPrice.text = "Rs. ${product.price}"

        val wished = holder.wished
        val wish = holder.wish

        // Load product image
        Picasso.get().load(product.productImage).into(holder.pImage, object : Callback {
            override fun onSuccess() {
                holder.loading.visibility = View.GONE
            }

            override fun onError(e: Exception?) {
                holder.loading.visibility = View.GONE
            }
        })

        // Handle adding to wishlist
        holder.wish.setOnClickListener {
            val model = WishlistModel(
                productId = product.productId,
                productImage = product.productImage,
                brandName = product.brandName,
                productName = product.productName,
                price = product.price
            )
            wishlistViewModel.addToWishlist(model) { success, message ->
                if (success) {
                    holder.wish.visibility = View.GONE
                    holder.wished.visibility = View.VISIBLE
                }
            }
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

    // Method to update data in the adapter
    fun updateData(products: List<ProductModel>) {
        data.clear()
        data.addAll(products)
        notifyDataSetChanged()
    }
}
