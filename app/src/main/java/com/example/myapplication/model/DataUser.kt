package com.example.myapplication.model

import java.io.Serializable

data class DataUser(
    val alamat: String,
    val nama: String,
    val nohp: String,
    val password: String,
    val user_id: String,
    val status: String,
    val username: String,
    val kota: String,
    val kodepos: String,
    val email : String
): Serializable