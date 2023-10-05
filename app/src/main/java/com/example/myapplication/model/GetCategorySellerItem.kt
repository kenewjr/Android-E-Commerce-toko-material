package com.example.myapplication.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GetCategorySellerItem(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("id")
    var id: Int,
    @SerializedName("name")
    val name: String,
):Parcelable