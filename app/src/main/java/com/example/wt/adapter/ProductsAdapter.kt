package com.example.wt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wt.R
import com.example.wt.model.ProductModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class ProductsAdapter(
    var context: Context,
    var data : ArrayList<ProductModel>
) : RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {
    class ProductViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val pImage : ImageView = itemView.findViewById(R.id.displayImage)
        val pName : TextView = itemView.findViewById(R.id.displayProdName)
        val pDesc : TextView = itemView.findViewById(R.id.displayDesc)
        val pPrice : TextView = itemView.findViewById(R.id.displayPrice)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductsAdapter.ProductViewHolder {
        val itemView : View = LayoutInflater.from(context).inflate(R.layout.sample_allproducts,parent,false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductsAdapter.ProductViewHolder, position: Int) {
        holder.pName.text = data[position].productName
        holder.pDesc.text = data[position].productDesc
        holder.pPrice.text = data[position].price.toString()

        Picasso.get().load(data[position].productImage).into(holder.pImage,object: Callback {
            override fun onSuccess() {
                // holder.loading.visibility = View.GONE
            }

            override fun onError(e: Exception?) {

            }
        })
    }

    override fun getItemCount(): Int {
        return data.size
    }

}