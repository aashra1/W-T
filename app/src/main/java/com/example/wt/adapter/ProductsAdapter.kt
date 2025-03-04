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
import com.example.wt.model.ProductModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class ProductsAdapter(
    var context: Context,
    var data: ArrayList<ProductModel>,
    private val onRemoveClick: (String) -> Unit
) : RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val loading: ProgressBar = itemView.findViewById(R.id.progressBar2)
        val pImage: ImageView = itemView.findViewById(R.id.displayImage)
        val bName: TextView = itemView.findViewById(R.id.displayBrandName)
        val pName: TextView = itemView.findViewById(R.id.displayProdName)
        val pPrice: TextView = itemView.findViewById(R.id.displayPrice)
        val btnRemove: ImageView = itemView.findViewById(R.id.removeProduct)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
        val itemView: View = LayoutInflater.from(context).inflate(R.layout.sample_allproducts, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = data[position]

        // Bind data to views
        holder.bName.text = product.brandName
        holder.pName.text = product.productName
        holder.pPrice.text = "Rs. ${product.price.toString()}"

        // Load image using Picasso
        Picasso.get().load(product.productImage).into(holder.pImage, object : Callback {
            override fun onSuccess() {
                holder.loading.visibility = View.GONE
            }

            override fun onError(e: Exception?) {
                // Handle error (optional)
            }
        })

        holder.btnRemove.setOnClickListener {
            onRemoveClick(product.productId)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun updateData(products: List<ProductModel>) {
        data.clear()
        data.addAll(products)
        notifyDataSetChanged()
    }

    fun getProductPosition(productId: String): Int {
        return data.indexOfFirst { it.productId == productId }
    }

    fun removeProduct(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }
}