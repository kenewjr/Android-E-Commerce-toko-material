package com.example.myapplication.model

import com.google.gson.annotations.SerializedName

data class PostSellerProduct(
    @SerializedName("id")
    val id: Int,
    @SerializedName("nama_produk")
    val namaProduk: String,
    @SerializedName("kategori_produk")
    val kategoriProduk: String,
    @SerializedName("deskripsi")
    val deskripsi: String,
    @SerializedName("stok")
    val stok: String,
    @SerializedName("harga")
    val harga: String,
    @SerializedName("gambar")
    val gambar: String,
    @SerializedName("create_at")
    val createAt: String,
    @SerializedName("update_at")
    val updateAt: String
)
