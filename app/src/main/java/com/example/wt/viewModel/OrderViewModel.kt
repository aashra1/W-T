package com.example.wt.viewModel

import androidx.lifecycle.ViewModel
import com.example.wt.model.OrderModel
import com.example.wt.repository.OrderRepository

class OrderViewModel(val repo: OrderRepository) {

    fun createOrder(
        order: OrderModel,
        callback : (Boolean, String) -> Unit
    ) {
        repo.createOrder(order,callback)
    }
}