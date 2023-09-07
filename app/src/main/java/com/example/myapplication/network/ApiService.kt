package com.example.myapplication.network

import com.example.myapplication.model.*
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import javax.inject.Singleton


interface ApiService {
    @FormUrlEncoded
    @POST("login_user")
    fun login(
        @Field("username")username : String,
        @Field("password")password : String
    ): Call<ResponseLogin>

    @FormUrlEncoded
    @POST("register_user")
    fun register(
        @Field("username")username : String,
        @Field("password")password : String,
        @Field("nama")nama : String,
        @Field("nohp")nohp : String,
        @Field("alamat")alamat : String
    ): Call<PostRegisterUser>

    @FormUrlEncoded
    @POST("ganti_password")
    fun changePassword(
        @Field("username")username : String,
        @Field("password")password : String
    ): Call<PostRegisterUser>

    @GET("get_userid")
    fun profileuser(
        @Query("user_id")id : Int
    ): Call<DataUser>

    @FormUrlEncoded
    @POST("update_user")
    fun updateuser(
        @Field("username")username : String,
        @Field("nama")nama : String,
        @Field("nohp")nohp : String,
        @Field("alamat")alamat : String
    ):Call<DataUser>

    @FormUrlEncoded
    @POST("tambah_barang")
    fun tambahbarang(
        @Field("nama_produk")namaProduk : String,
        @Field("kategori_produk")kategoriProduk : String,
        @Field("deskripsi")deskripsi : String,
        @Field("stok")stok : String,
        @Field("harga")harga : String,
        @Field("gambar")gambar : String
    ):Call<PostSellerProduct>

    @GET("get_all_kategori")
    suspend fun GetCategory() : List<GetCategorySellerItem>

    @GET("produk_search")
    fun searchproduk(
        @Query("search")search:String
    ):Call<List<GetAllProdukItem>>

    @GET("get_all_produk")
    suspend fun getallproduk():List<GetAllProdukItem>

    @GET("getproduk_byid")
    fun getprodukbyid(
        @Query("id")id : Int
    ):Call<GetAllProdukItem>

}