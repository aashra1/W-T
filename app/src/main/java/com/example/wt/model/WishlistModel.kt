package com.example.wt.model

import android.os.Parcel
import android.os.Parcelable

data class WishlistModel(
    var wishlistId : String = "",
    var userId : String = "",
    var productId : String = "",
    var productImage : String = "",
    var brandName : String = "",
    var productName : String = "",
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
    )

    override fun describeContents(): Int {
        return 0;
    }

    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeString(wishlistId)
        parcel.writeString(userId)
        parcel.writeString(productId)
        parcel.writeString(productImage)
        parcel.writeString(brandName)
        parcel.writeString(productName)
    }

    companion object CREATOR : Parcelable.Creator<WishlistModel> {
        override fun createFromParcel(parcel: Parcel): WishlistModel {
            return WishlistModel(parcel)
        }

        override fun newArray(size: Int): Array<WishlistModel?> {
            return arrayOfNulls(size)
        }
    }
}