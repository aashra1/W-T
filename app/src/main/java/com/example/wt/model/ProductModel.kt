package com.example.wt.model

import android.icu.text.DecimalFormat
import android.media.Image
import android.os.Parcel
import android.os.Parcelable

data class ProductModel(
    var productId : String = "",
    var productImage : String = "",
    var productName : String = "",
    var productDesc : String = "",
    var price : Int = 0
) : Parcelable  {
    constructor(parcel: Parcel) : this(
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readInt()?:0,
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(productId)
        parcel.writeString(productImage)
        parcel.writeString(productName)
        parcel.writeString(productDesc)
        parcel.writeInt(price)
    }

    companion object CREATOR : Parcelable.Creator<ProductModel> {
        override fun createFromParcel(parcel: Parcel): ProductModel {
            return ProductModel(parcel)
        }

        override fun newArray(size: Int): Array<ProductModel?> {
            return arrayOfNulls(size)
        }
    }
}