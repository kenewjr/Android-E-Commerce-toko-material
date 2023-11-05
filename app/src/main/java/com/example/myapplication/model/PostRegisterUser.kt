package com.example.myapplication.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostRegisterUser(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("nama")
    val nama: String,
    @SerializedName("nohp")
    val nohp: String,
    @SerializedName("alamat")
    val alamat: String,
    @SerializedName("kota")
    val kota: String,
    @SerializedName("kodepos")
    val kodepos: String,
    @SerializedName("email")
    val email: String
):Parcelable
