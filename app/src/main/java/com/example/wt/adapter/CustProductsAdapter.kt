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
import com.example.wt.adapter.ProductsAdapter.ProductViewHolder
import com.example.wt.model.ProductModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class CustProductsAdapter(
    var context: Context,
    var data: ArrayList<ProductModel>
) : RecyclerView.Adapter<CustProductsAdapter.CustProductViewHolder>() {
    class CustProductViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val loading : ProgressBar = itemView.findViewById(R.id.progressBar3)
        val pImage : ImageView = itemView.findViewById(R.id.newDisplayImage)
        val bName : TextView = itemView.findViewById(R.id.newDisplayBrandName)
        val pName : TextView = itemView.findViewById(R.id.newDisplayProdName)
        val pPrice : TextView = itemView.findViewById(R.id.newDisplayPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustProductViewHolder {
        val itemView : View = LayoutInflater.from(context).inflate(R.layout.sample_viewprod,parent,false)
        return CustProductViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: CustProductViewHolder, position: Int) {
        holder.bName.text = data[position].brandName
        holder.pName.text = data[position].productName
        holder.pPrice.text = data[position].price.toString()

        Picasso.get().load(data[position].productImage).into(holder.pImage, object : Callback {
            override fun onSuccess() {
                holder.loading.visibility = View.GONE
            }

            override fun onError(e: Exception?) {

            }
        })
    }

    fun updateData(products: List<ProductModel>){
        data.clear()
        data.addAll(products)
        notifyDataSetChanged()
    }

    fun getProductId(position: Int) : String{
        return data[position].productId
    }

}