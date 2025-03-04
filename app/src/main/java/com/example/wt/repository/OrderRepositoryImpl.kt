package com.example.wt.repository

import com.example.wt.model.OrderModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class OrderRepositoryImpl : OrderRepository {

    val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref : DatabaseReference = database.reference.child("Orders")

    override fun createOrder(
        order: OrderModel,
        callback: (Boolean, String) -> Unit
    ) {
        var id = ref.push().key.toString()
        order.orderId = id
        ref.child(id).setValue(order).addOnCompleteListener{
            if (it.isSuccessful) {
                callback(true, "Order Added Successfully")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }
}