package com.example.myapplication.model

data class GetKomentarItem(
    val create_at: String,
    val id: String,
    val id_produk: String,
    val id_user: String,
    val komentar: String,
    val nama_pembeli: String
)