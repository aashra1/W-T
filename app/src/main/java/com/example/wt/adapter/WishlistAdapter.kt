package com.example.wt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wt.R
import com.example.wt.model.WishlistModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class WishlistAdapter(
    var context: Context,
    var data: ArrayList<WishlistModel>
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
        val currentItem = data[position]

        holder.bName.text = currentItem.brandName
        holder.pName.text = currentItem.productName
        holder.pPrice.text = "Rs. ${currentItem.price}"

        Picasso.get().load(currentItem.productImage).into(holder.pImage, object : Callback {
            override fun onSuccess() {
                holder.loading.visibility = View.GONE
            }

            override fun onError(e: Exception?) {
                holder.loading.visibility = View.GONE
            }
        })

    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun updateData(newData: List<WishlistModel>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }
}
