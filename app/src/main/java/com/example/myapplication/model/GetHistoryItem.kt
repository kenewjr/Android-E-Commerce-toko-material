package com.example.myapplication.model

data class GetHistoryItem(
    val gambar: String,
    val harga_produk: String,
    val id: String,
    val id_produk: String,
    val id_user: String,
    val order_id: String,
    val alamat: String,
    val ongkos: String,
    val jumlah_produk: String,
    val nama_pembeli: String,
    val nama_produk: String,
    val tgl_transaksi: String,
    val status: String,
    val total_harga: String,
    val tujuan_rekening : String,
    val nama_rekening : String,
    val dibaca : String,
):java.io.Serializable