package com.example.wt.model

import android.os.Parcel
import android.os.Parcelable

data class OrderModel(
    var orderId: String = "", // Unique ID for the order
    val userId: String = "", // ID of the user who placed the order
    val items: List<CartModel> = emptyList(), // List of items in the order
    val totalPrice: Double = 0.0, // Total price of the order
    val timestamp: Long = System.currentTimeMillis(), // Timestamp of the order
    var orderStatus: String = "Confirmed" // Default order status
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createTypedArrayList(CartModel.CREATOR) ?: emptyList(),
        parcel.readDouble(),
        parcel.readLong(),
        parcel.readString() ?: "Confirmed"
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(orderId)
        parcel.writeString(userId)
        parcel.writeTypedList(items)
        parcel.writeDouble(totalPrice)
        parcel.writeLong(timestamp)
        parcel.writeString(orderStatus)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrderModel> {
        override fun createFromParcel(parcel: Parcel): OrderModel {
            return OrderModel(parcel)
        }

        override fun newArray(size: Int): Array<OrderModel?> {
            return arrayOfNulls(size)
        }
    }
}