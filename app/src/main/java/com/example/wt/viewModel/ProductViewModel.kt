package com.example.wt.viewModel

import androidx.lifecycle.MutableLiveData
import com.example.wt.model.ProductModel
import com.example.wt.repository.ProductRepository

class ProductViewModel(val repo : ProductRepository) {

    fun addProduct(
        productModel: ProductModel,
        callback : (Boolean, String) -> Unit
    ){
        repo.addProduct(productModel,callback)
    }

    fun updateProduct(
        productId : String,
        data : MutableMap<String,Any>,
                      callback: (Boolean, String) -> Unit
    ){
        repo.updateProduct(productId,data,callback)
    }

    fun deleteProduct(
        productId: String,
        callback: (Boolean, String) -> Unit
    ){
        repo.deleteProduct(productId,callback)
    }


    var _products = MutableLiveData<ProductModel>()
    var products = MutableLiveData<ProductModel>()
        get() = _products

    fun getProductById(productId: String, ){
        repo.getProductById(productId){
            products,success,message ->
            if(success){
                _products.value = products
            }
        }
    }


    var _allProducts = MutableLiveData<List<ProductModel>>()
    var allproducts = MutableLiveData<List<ProductModel>>()
        get() = _allProducts

    fun getAllProduct(){
        repo.getAllProduct{
            products, sucess, message ->
            if(sucess){
                _allProducts.value = products
            }
        }
    }

}