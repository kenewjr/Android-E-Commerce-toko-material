package com.example.myapplication.model

data class GetPromoItem(
    val harga_diskon: String,
    val id: String,
    val max_harga: String,
    val min_harga: String
):java.io.Serializable