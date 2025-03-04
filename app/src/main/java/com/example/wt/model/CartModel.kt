package com.example.wt.model

import android.os.Parcel
import android.os.Parcelable


data class CartModel(
    var cartId : String = "",
    var userId : String = "",
    var productId: String = "",
    var productImage : String = "",
    var brandName : String = "",
    var productName: String = "",
    var price : Int = 0,
    var quantity: Long = 1,

) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readInt()?:0,
        parcel.readLong()?:0
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(cartId)
        parcel.writeString(userId)
        parcel.writeString(productId)
        parcel.writeString(productImage)
        parcel.writeString(brandName)
        parcel.writeString(productName)
        parcel.writeInt(price)
        parcel.writeLong(quantity)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CartModel> {
        override fun createFromParcel(parcel: Parcel): CartModel {
            return CartModel(parcel)
        }

        override fun newArray(size: Int): Array<CartModel?> {
            return arrayOfNulls(size)
        }
    }
}