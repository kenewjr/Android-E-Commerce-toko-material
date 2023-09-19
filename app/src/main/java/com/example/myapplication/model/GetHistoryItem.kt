package com.example.myapplication.model

data class GetHistoryItem(
    val gambar: String,
    val harga_produk: String,
    val id: String,
    val id_produk: String,
    val id_user: String,
    val jumlah_produk: String,
    val nama_pembeli: String,
    val nama_produk: String,
    val tgl_transaksi: String,
    val total_harga: String
):java.io.Serializable