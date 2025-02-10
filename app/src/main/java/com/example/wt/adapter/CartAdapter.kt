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
import com.example.wt.R
import com.example.wt.model.ProductModel
import com.squareup.picasso.Picasso

class CartAdapter(
    private val context: Context,
    private val cartItems: ArrayList<ProductModel>,
    private val onQuantityChange: (ProductModel, Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.product)
        val brandName: TextView = itemView.findViewById(R.id.lblbrand)
        val productName: TextView = itemView.findViewById(R.id.lblProdName)
        val size: TextView = itemView.findViewById(R.id.lblsize)
        val productId: TextView = itemView.findViewById(R.id.lblProdId)
        val quantitySpinner: Spinner = itemView.findViewById(R.id.QuantitySpinner)
        val price: TextView = itemView.findViewById(R.id.lblprice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.sample_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = cartItems[position]
        holder.brandName.text = product.brandName
        holder.productName.text = product.productName
        holder.size.text = product.size
        holder.productId.text = product.productId
        holder.price.text = "Rs. ${product.price}"

        // Load image using Picasso
        Picasso.get().load(product.productImage).placeholder(R.drawable.placeholder).into(holder.productImage)

        // Handle quantity change
        holder.quantitySpinner.setSelection(product.quantity - 1)
        holder.quantitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                onQuantityChange(product, pos + 1)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun getItemCount(): Int = cartItems.size
}