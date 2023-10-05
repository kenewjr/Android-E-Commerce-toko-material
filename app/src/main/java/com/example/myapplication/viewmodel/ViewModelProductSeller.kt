package com.example.myapplication.viewmodel

import and5.abrar.e_commerce.repository.ProductRepository
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.*
import com.example.myapplication.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Field
import javax.inject.Inject

@HiltViewModel
class ViewModelProductSeller @Inject constructor(private var productRepository: ProductRepository, api: ApiService) : ViewModel() {
    var sellerCategory = MutableLiveData<List<GetCategorySellerItem>>()

    private val livedataJualProduct = MutableLiveData<PostSellerProduct>()

    private val livedatabuyerorder = MutableLiveData<GetHistoryItem>()

    private val livedatahistory = MutableLiveData<List<GetHistoryItem>>()
    val datahistory : LiveData<List<GetHistoryItem>> = livedatahistory
    val historyList: List<GetHistoryItem>? = datahistory.value

    private val deleteProduct = MutableLiveData<com.example.myapplication.model.Response>()

    private val updateproduct = MutableLiveData<com.example.myapplication.model.Response>()

    private val apiServices = api
    fun getSellerCategory(){
        viewModelScope.launch {
            val category = productRepository.getSellerCategory()
            sellerCategory.value = category
        }
    }

    fun editProduct(
       id : Int,
        namaProduk : String,
        kategoriProduk : String,
       deskripsi : String,
       stok : String,
        harga : String,
        gambar : String
    ){
        apiServices.editProduk(id, namaProduk, kategoriProduk, deskripsi, stok, harga, gambar)
            .enqueue(object : Callback<com.example.myapplication.model.Response>{
                override fun onResponse(
                    call: Call<com.example.myapplication.model.Response>,
                    response: Response<com.example.myapplication.model.Response>
                ) {
                    updateproduct.value = response.body()
                }

                override fun onFailure(
                    call: Call<com.example.myapplication.model.Response>,
                    t: Throwable
                ) {
                   //
                }

            })
    }
    fun deleteProduct(
        delete : String,
        id: Int
    ){
        apiServices.deleteProduk(
            delete,
            id
        ).enqueue(object : Callback<com.example.myapplication.model.Response>{
            override fun onResponse(
                call: Call<com.example.myapplication.model.Response>,
                response: Response<com.example.myapplication.model.Response>
            ) {
                if(response.isSuccessful){
                    deleteProduct.value = response.body()
                }
            }

            override fun onFailure(call: Call<com.example.myapplication.model.Response>, t: Throwable) {
                //
            }

        })
    }
    fun tambahHistory(
        idUser : Int,
        idProduk : Int,
        namaUser : String,
        order_id : String,
        tglTransaksi : String,
        namaProduk : String,
        hargaProduk : String,
        totalHarga : String,
        jumlahProduk : String,
        gambar : String
    ){
    apiServices.tambahHistory(idUser,idProduk,order_id,namaUser,tglTransaksi,namaProduk,hargaProduk,totalHarga,jumlahProduk,gambar)
        .enqueue(object : Callback<GetHistoryItem>{
            override fun onResponse(
                call: Call<GetHistoryItem>,
                response: Response<GetHistoryItem>
            ) {
                if (response.isSuccessful){
                    livedatabuyerorder.value = response.body()
                }
            }

            override fun onFailure(call: Call<GetHistoryItem>, t: Throwable) {
                //
            }
        })

    }

    fun getHistory()
    {
        apiServices.getHistory().enqueue(object : Callback<List<GetHistoryItem>>{
            override fun onResponse(
                call: Call<List<GetHistoryItem>>,
                response: Response<List<GetHistoryItem>>
            ) {
                if (response.isSuccessful){
                    livedatahistory.value = response.body()
                }
            }

            override fun onFailure(call: Call<List<GetHistoryItem>>, t: Throwable) {
                //
            }


        })
    }
    fun jualproduct(
        nama : String,
        desc : String,
        harga : String,
        category : String,
        stok : String,
        image: String){
        apiServices.tambahbarang(nama,category,desc,stok,harga,image)
            .enqueue(object : Callback<PostSellerProduct> {
                override fun onResponse(
                    call: Call<PostSellerProduct>,
                    response: Response<PostSellerProduct>
                ) {
                    if (response.isSuccessful) {
                        livedataJualProduct.value = response.body()
                    }
                }
                override fun onFailure(call: Call<PostSellerProduct>, t: Throwable) {
                    //
                }
            })
    }
}