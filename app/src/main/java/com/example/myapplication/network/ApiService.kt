@file:Suppress("FunctionName")

package com.example.myapplication.network

import com.example.myapplication.model.*
import com.example.myapplication.model.history.GetRequired
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface ApiService {
    @FormUrlEncoded
    @POST("login_user.php")
    fun login(
        @Field("username")username : String,
        @Field("password")password : String
    ): Call<ResponseLogin>

    @FormUrlEncoded
    @POST("register_user.php")
    fun register(
        @Field("username")username : String,
        @Field("password")password : String,
        @Field("nama")nama : String,
        @Field("nohp")nohp : String,
        @Field("alamat")alamat : String,
        @Field("kota")kota : String,
        @Field("kodepos")kodepos : String,
        @Field("email")email: String
    ): Call<PostRegisterUser>

    @FormUrlEncoded
    @POST("ganti_password.php")
    fun changePassword(
        @Field("username")username : String,
        @Field("password")password : String
    ): Call<PostRegisterUser>

    @GET("get_userid")
    fun profileuser(
        @Query("user_id")id : Int
    ): Call<DataUser>

    @FormUrlEncoded
    @POST("update_user.php")
    fun updateuser(
        @Field("username")username : String,
        @Field("nama")nama : String,
        @Field("nohp")nohp : String,
        @Field("alamat")alamat : String,
        @Field("kota")kota : String,
        @Field("kodepos")kodepos : String,
        @Field("email")email : String
    ):Call<DataUser>

    @FormUrlEncoded
    @POST("tambah_barang.php")
    fun tambahbarang(
        @Field("nama_produk")namaProduk : String,
        @Field("kategori_produk")kategoriProduk : String,
        @Field("deskripsi")deskripsi : String,
        @Field("berat")berat : String,
        @Field("stok")stok : String,
        @Field("harga")harga : String,
        @Field("gambar")gambar : String
    ):Call<PostSellerProduct>

    @GET("get_all_kategori.php")
    suspend fun GetCategory() : List<GetCategorySellerItem>

    @GET("produk_search.php")
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
    @POST("delete_produk.php")
    fun deleteProduk(
        @Field("delete")delete :String,
        @Field("id")id: Int
    ):Call<Response>

    @FormUrlEncoded
    @POST("tambah_history.php")
    fun tambahHistory(
        @Field("id_user")id_user :Int,
        @Field("id_produk")id_produk: Int,
        @Field("order_id")order_id: String,
        @Field("nama_pembeli")nama_pembeli: String,
        @Field("alamat")alamat: String,
        @Field("tgl_transaksi")tgl_transaksi: String,
        @Field("nama_produk")nama_produk: String,
        @Field("harga_produk")harga_produk: String,
        @Field("total_harga")total_harga: String,
        @Field("jumlah_produk")jumlah_produk: String,
        @Field("gambar")gambar: String,
        @Field("ongkos")ongkos : String,
        @Field("tujuan_rekening")tujuan_rekening: String,
        @Field("nama_rekening")nama_rekening : String
    ):Call<GetHistoryItem>

    @GET("get_history.php")
    fun getHistory():Call<List<GetHistoryItem>>

    @GET("get_historybyUserid.php")
    fun getHistoryUserID(
        @Query("id_user")id_user : Int
    ):Call<List<GetHistoryItem>>
    @GET("get_historybyUid&Pid.php")
    fun getOrder(
        @Query("id_user")id_user : Int,
        @Query("id_produk")id_produk : Int
    ):Call<GetHistoryItem>
    @FormUrlEncoded
    @POST("tambah_komentar.php")
    fun tambahKomentar(
        @Field("id_user")id_user :Int,
        @Field("komentar")komentar: String,
        @Field("nama_pembeli")nama_pembeli: String,
        @Field("id_produk")id_produk: Int,
        @Field("rating")rating: Float
    ):Call<Response>

    @GET("get_komentarPid.php")
    fun getKomentar(
        @Query("id_produk")id_produk : Int
    ):Call<List<GetKomentarItem>>

    @POST("edit_produk")
    @Multipart
    fun editProduk(
        @Part("id")id : RequestBody,
        @Part("nama_produk")namaProduk : RequestBody,
        @Part("kategori_produk")kategoriProduk : RequestBody,
        @Part("deskripsi")deskripsi : RequestBody,
        @Part("stok")stok : RequestBody,
        @Part("harga")harga : RequestBody,
        @Part("berat")berat : RequestBody,
        @Part gambar : MultipartBody.Part
    ):Call<Response>

    @POST("edit_produkNG")
    @Multipart
    fun editProdukNG(
        @Part("id")id : RequestBody,
        @Part("nama_produk")namaProduk : RequestBody,
        @Part("kategori_produk")kategoriProduk : RequestBody,
        @Part("deskripsi")deskripsi : RequestBody,
        @Part("stok")stok : RequestBody,
        @Part("harga")harga : RequestBody,
        @Part("berat")berat : RequestBody
    ):Call<Response>

    @FormUrlEncoded
    @POST("delete_category")
    fun deleteCtgy(
        @Field("id")id:Int
    ):Call<Response>

    @FormUrlEncoded
    @POST("edit_category")
    fun editCtgy(
        @Field("id")id:Int,
        @Field("name")name:String
    ):Call<Response>

    @FormUrlEncoded
    @POST("tambah_category")
    fun tambahCtgy(
        @Field("name")name:String
    ):Call<Response>

    @GET("get_all_pengiriman")
    suspend fun getPengiriman():List<GetAllPengirimanItem>

    @FormUrlEncoded
    @POST("tambah_pengiriman")
    fun tambahPengiriman(
        @Field("kendaraan")kendaraan:String,
        @Field("harga")harga:String,
        @Field("max_berat")max_berat:String
    ):Call<Response>

    @FormUrlEncoded
    @POST("edit_pengiriman")
    fun editPengiriman(
        @Field("id")id:Int,
        @Field("kendaraan")kendaraan:String,
        @Field("harga")harga:String,
        @Field("max_berat")max_berat:String
    ):Call<Response>

    @FormUrlEncoded
    @POST("delete_pengiriman")
    fun deletePengiriman(
        @Field("id")id:Int
    ):Call<Response>

    @GET("midtrans_notification.php")
    fun getmidtransNotif():Call<Response>

    @GET("filter_category")
    fun getFilterCategory(
        @Query("kategori")kategori : Int
    ):Call<List<GetAllProdukItem>>

    @FormUrlEncoded
    @POST("edit_dibaca")
    fun updateDibaca(
        @Field("id")id:Int,
        @Field("dibaca")dibaca:String
    ):Call<Response>

    @FormUrlEncoded
    @POST("update_history_status")
    fun updatehisotryStatus(
        @Field("id")id:Int,
        @Field("status")status:String
    ):Call<Response>

    @GET("required_history")
    fun getRequiredHistory(
        @Query("id")id:Int,
        @Query("ORDER_ID")orderId:String
    ):Call<GetRequired>

    @FormUrlEncoded
    @POST("lupa_password")
    fun LupaPassword(
        @Field("input")input : String
    ):Call<ResponseLupaPasswordItem>

    @FormUrlEncoded
    @POST("lupa_password_email")
    fun lupaPasswordEmail(
        @Field("email")input : String
    ):Call<ResponseLupaPasswordItem>

    @GET("get_all_promo")
    suspend fun getPromo():List<GetPromoItem>

    @FormUrlEncoded
    @POST("edit_promo")
    fun editPromo(
        @Field("id")id: Int,
        @Field("min_harga")min_harga:String,
        @Field("max_harga")max_harga:String,
        @Field("harga_diskon")harga_diskon:String,
    ):Call<Response>

    @FormUrlEncoded
    @POST("tambah_promo")
    fun tambahpromo(
        @Field("min_harga")min_harga:String,
        @Field("max_harga")max_harga:String,
        @Field("harga_diskon")harga_diskon:String,
    ):Call<Response>

    @FormUrlEncoded
    @POST("delete_promo")
    fun deletepromo(
        @Field("id")id: Int
    ):Call<Response>

    @GET("total_penjualan")
    fun total_penjualan():Call<GetTotalItem>
}