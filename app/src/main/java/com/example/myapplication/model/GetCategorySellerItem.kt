package com.example.myapplication.model


import com.google.gson.annotations.SerializedName

data class GetCategorySellerItem(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("id")
    var id: Int,
    @SerializedName("name")
    val name: String,
):java.io.Serializable