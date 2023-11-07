package com.example.myapplication.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GetAllProdukItem(
    @SerializedName("create_at")
    val create_at: String,
    @SerializedName("update_at")
    val update_at: String,
    @SerializedName("nama_produk")
    val nama: String,
    @SerializedName("deskripsi")
    val deskripsi: String,
    @SerializedName("gambar")
    val gambar: String,
    @SerializedName("berat")
    val berat: String,
    @SerializedName("harga")
    val harga: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("kategori")
    val kategori: String,
    @SerializedName("stok")
    val stok: String,
    @SerializedName("rating")
    val rating: Float,
    @SerializedName("ratinguser")
    val ratinguser: String
): Serializable