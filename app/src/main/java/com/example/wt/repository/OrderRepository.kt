package com.example.wt.repository

import com.example.wt.model.OrderModel
import com.google.firebase.database.DatabaseReference

interface OrderRepository {
    fun createOrder(order: OrderModel, callback: (Boolean, String) -> Unit)
}