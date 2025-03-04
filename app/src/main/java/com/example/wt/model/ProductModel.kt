package com.example.wt.model

import android.icu.text.DecimalFormat
import android.media.Image
import android.os.Parcel
import android.os.Parcelable
import android.widget.Spinner

data class ProductModel(
    var productId : String = "",
    var productImage : String = "",
    var brandName : String = "",
    var productName : String = "",
    var price : Int = 0,
    var quantity : Int = 0
) : Parcelable  {
    constructor(parcel: Parcel) : this(
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readInt()?:0,
        parcel.readInt()?:0
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(productId)
        parcel.writeString(productImage)
        parcel.writeString(brandName)
        parcel.writeString(productName)
        parcel.writeInt(price)
        parcel.writeInt(quantity)
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