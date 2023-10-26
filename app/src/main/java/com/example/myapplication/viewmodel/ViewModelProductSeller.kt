package com.example.myapplication.viewmodel


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.*
import com.example.myapplication.network.ApiService
import com.example.myapplication.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ViewModelProductSeller @Inject constructor(private var productRepository: ProductRepository, api: ApiService) : ViewModel() {
    var sellerCategory = MutableLiveData<List<GetCategorySellerItem>>()
    var sellerPengiriman = MutableLiveData<List<GetAllPengirimanItem>>()

    private val livedataJualProduct = MutableLiveData<PostSellerProduct>()

    private val livedatabuyerorder = MutableLiveData<GetHistoryItem>()

    private val livedatahistory = MutableLiveData<List<GetHistoryItem>>()
    val datahistory : LiveData<List<GetHistoryItem>> = livedatahistory

    private val deleteProduct = MutableLiveData<com.example.myapplication.model.Response>()

    private val deleteCategory = MutableLiveData<com.example.myapplication.model.Response>()

    private val editCategory = MutableLiveData<com.example.myapplication.model.Response>()

    private val tambahCategory = MutableLiveData<com.example.myapplication.model.Response>()

    private val pengiriman = MutableLiveData<com.example.myapplication.model.Response>()

    private val updateproduct = MutableLiveData<com.example.myapplication.model.Response>()

    private val apiServices = api
    fun getSellerCategory(){
        viewModelScope.launch {
            val category = productRepository.getSellerCategory()
            sellerCategory.value = category
        }
    }

    fun getSellerPengiriman(){
        viewModelScope.launch {
            val pengiriman = productRepository.getPengiriman()
            sellerPengiriman.value=pengiriman
        }
    }

    fun tambahPengiriman(kendaraan:String,harga: String,berat: String){
        apiServices.tambahPengiriman(kendaraan,harga,berat).enqueue(object : Callback<com.example.myapplication.model.Response>{
            override fun onResponse(
                call: Call<com.example.myapplication.model.Response>,
                response: Response<com.example.myapplication.model.Response>
            ) {
                if(response.isSuccessful){
                    pengiriman.value = response.body()
                }else {
                    //
                }
            }

            override fun onFailure(
                call: Call<com.example.myapplication.model.Response>,
                t: Throwable
            ) {
                //
            }
        })
    }

    fun editPengiriman(id: Int,kendaraan:String,harga: String,berat: String){
        apiServices.editPengiriman(id,kendaraan,harga,berat).enqueue(object : Callback<com.example.myapplication.model.Response>{
            override fun onResponse(
                call: Call<com.example.myapplication.model.Response>,
                response: Response<com.example.myapplication.model.Response>
            ) {
                if(response.isSuccessful){
                    pengiriman.value = response.body()
                }else {
                    //
                }
            }

            override fun onFailure(
                call: Call<com.example.myapplication.model.Response>,
                t: Throwable
            ) {
                //
            }
        })
    }

    fun deletePengiriman(id: Int){
        apiServices.deletePengiriman(id).enqueue(object : Callback<com.example.myapplication.model.Response>{
            override fun onResponse(
                call: Call<com.example.myapplication.model.Response>,
                response: Response<com.example.myapplication.model.Response>
            ) {
                if(response.isSuccessful){
                    pengiriman.value = response.body()
                }else {
                    //
                }
            }

            override fun onFailure(
                call: Call<com.example.myapplication.model.Response>,
                t: Throwable
            ) {
                //
            }
        })
    }

    fun tambahCtgy(name: String){
        apiServices.tambahCtgy(name).enqueue(object : Callback<com.example.myapplication.model.Response>{
            override fun onResponse(
                call: Call<com.example.myapplication.model.Response>,
                response: Response<com.example.myapplication.model.Response>
            ) {
                if(response.isSuccessful){
                    tambahCategory.value = response.body()
                }else {
                    //
                }
            }

            override fun onFailure(
                call: Call<com.example.myapplication.model.Response>,
                t: Throwable
            ) {
                //
            }

        })
    }
    fun editCtgy(id: Int,name:String){
        apiServices.editCtgy(id, name)
            .enqueue(object : Callback<com.example.myapplication.model.Response>{
                override fun onResponse(
                    call: Call<com.example.myapplication.model.Response>,
                    response: Response<com.example.myapplication.model.Response>
                ) {
                    if(response.isSuccessful){
                        editCategory.value = response.body()
                    }else {
                        //
                    }
                }

                override fun onFailure(
                    call: Call<com.example.myapplication.model.Response>,
                    t: Throwable
                ) {
                    //
                }

            })    }
    fun deleteCtgy(id: Int){
        apiServices.deleteCtgy(id).enqueue(object : Callback<com.example.myapplication.model.Response>{
            override fun onResponse(
                call: Call<com.example.myapplication.model.Response>,
                response: Response<com.example.myapplication.model.Response>
            ) {
                if(response.isSuccessful){
                    deleteCategory.value = response.body()

                }else {
                    //
                }
            }

            override fun onFailure(
                call: Call<com.example.myapplication.model.Response>,
                t: Throwable
            ) {
                //
            }

        })
    }

    fun editProduct(
        id : RequestBody,
        namaProduk : RequestBody,
        kategoriProduk : RequestBody,
        deskripsi : RequestBody,
        stok : RequestBody,
        harga : RequestBody,
        berat : RequestBody,
        gambar : MultipartBody.Part
    ){
        apiServices.editProduk(id, namaProduk, kategoriProduk, deskripsi, stok, harga, berat, gambar)
            .enqueue(object : Callback<com.example.myapplication.model.Response>{
                override fun onResponse(
                    call: Call<com.example.myapplication.model.Response>,
                    response: Response<com.example.myapplication.model.Response>
                ) {
                    if(response.isSuccessful){
                        updateproduct.value = response.body()
                        Log.e("error1",response.body().toString())
                    }else {
                        Log.e("error2",response.message())
                        Log.e("error2",response.errorBody().toString())
                        Log.e("error2",response.body().toString())
                    }
                }
                override fun onFailure(
                    call: Call<com.example.myapplication.model.Response>,
                    t: Throwable
                ) {
                    Log.e("error3",t.toString())
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
        order_id : String,
        namaUser : String,
        alamat : String,
        tglTransaksi : String,
        namaProduk : String,
        hargaProduk : String,
        totalHarga : String,
        jumlahProduk : String,
        gambar : String,
        ongkos : String
    ){
        apiServices.tambahHistory(idUser,idProduk,order_id,namaUser,alamat,tglTransaksi,namaProduk,hargaProduk,totalHarga,jumlahProduk,gambar,ongkos)
            .enqueue(object : Callback<GetHistoryItem>{
                override fun onResponse(
                    call: Call<GetHistoryItem>,
                    response: Response<GetHistoryItem>
                ) {
                    if (response.isSuccessful){
                        livedatabuyerorder.value = response.body()
                    }else {
                        Log.e("respone",response.message())
                    }
                }

                override fun onFailure(call: Call<GetHistoryItem>, t: Throwable) {
                    Log.e("respone",t.message.toString())
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
        berat : String,
        category : String,
        stok : String,
        image: String){
        apiServices.tambahbarang(nama,category,desc,berat,stok,harga,image)
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