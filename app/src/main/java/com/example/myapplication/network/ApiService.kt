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

    @GET("get_userid.php")
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

    @GET("get_all_kategori.php")
    suspend fun GetCategory() : List<GetCategorySellerItem>

    @GET("produk_search")
    fun searchproduk(
        @Query("search")search:String
    ):Call<List<GetAllProdukItem>>

    @GET("get_all_produk.php")
    suspend fun getallproduk():List<GetAllProdukItem>

    @GET("getproduk_byid.php")
    fun getprodukbyid(
        @Query("id")id : Int
    ):Call<GetDataProductSellerItemItem>

    @FormUrlEncoded
    @POST("delete_produk")
    fun deleteProduk(
        @Field("delete")delete :String,
        @Field("id")id: Int
    ):Call<Response>

    @FormUrlEncoded
    @POST("tambah_history.php")
    fun tambahHistory(
        @Field("id_user")id_user :Int,
        @Field("id_produk")id_produk: Int,
        @Field("nama_pembeli")nama_pembeli: String,
        @Field("tgl_transaksi")tgl_transaksi: String,
        @Field("nama_produk")nama_produk: String,
        @Field("harga_produk")harga_produk: String,
        @Field("total_harga")total_harga: String,
        @Field("jumlah_produk")jumlah_produk: String,
        @Field("gambar")gambar: String
    ):Call<GetHistoryItem>

    @GET("get_history.php")
    fun getHistory():Call<List<GetHistoryItem>>

    @GET("get_historybyUserid.php")
    fun getHistoryUserID(
        @Query("id_user")id_user : Int
    ):Call<List<GetHistoryItem>>
}