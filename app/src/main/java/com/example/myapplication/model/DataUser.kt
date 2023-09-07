package com.example.myapplication.model

import java.io.Serializable

data class DataUser(
    val alamat: String,
    val nama: String,
    val nohp: String,
    val password: String,
    val user_id: String,
    val username: String
): Serializable