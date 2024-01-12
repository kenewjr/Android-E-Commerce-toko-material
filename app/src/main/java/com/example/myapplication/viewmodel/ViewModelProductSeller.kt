@file:Suppress("EmptyMethod", "EmptyMethod")

package com.example.myapplication.viewmodel


import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.*
import com.example.myapplication.model.history.GetRequired
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
    var sellerPromo = MutableLiveData<List<GetPromoItem>>()

    private val livedataJualProduct = MutableLiveData<PostSellerProduct>()

    private val livedatahistory = MutableLiveData<List<GetHistoryItem>>()
    val datahistory : LiveData<List<GetHistoryItem>> = livedatahistory

    private val livedataRequire = MutableLiveData<GetRequired>()
    val dataRequried : LiveData<GetRequired> = livedataRequire

    private val livedataTotal = MutableLiveData<GetTotalItem>()
    val dataTotal : LiveData<GetTotalItem> = livedataTotal

    private val deleteProduct = MutableLiveData<com.example.myapplication.model.Response>()

    private val deleteCategory = MutableLiveData<com.example.myapplication.model.Response>()

    private val editCategory = MutableLiveData<com.example.myapplication.model.Response>()

    private val tambahCategory = MutableLiveData<com.example.myapplication.model.Response>()

    private val pengiriman = MutableLiveData<com.example.myapplication.model.Response>()

    private val updateproduct = MutableLiveData<com.example.myapplication.model.Response>()

    private val respon = MutableLiveData<com.example.myapplication.model.Response>()

    private val apiServices = api
    @SuppressLint("NullSafeMutableLiveData")
    fun getSellerCategory(){
        viewModelScope.launch {
            try {
                val category = productRepository.getSellerCategory()
                sellerCategory.value = category
                if (category.isEmpty()) {
                    sellerCategory.value = null
                } else {
                    sellerCategory.value = category
                }
            } catch (e: Exception) {
                // Handle network failures or exceptions here
                Log.e("NetworkError", e.message, e)
            }

        }
    }


    @SuppressLint("NullSafeMutableLiveData")
    fun getSellerPengiriman(){
        viewModelScope.launch {
            try{
                val pengiriman = productRepository.getPengiriman()
                if (pengiriman.isEmpty()) {
                    sellerPengiriman.value=null
                } else {
                    sellerPengiriman.value=pengiriman
                }
            } catch (e: Exception) {
                // Handle network failures or exceptions here
                Log.e("NetworkError", e.message, e)
            }
        }
    }
    @SuppressLint("NullSafeMutableLiveData")
    fun getPromo(){
        viewModelScope.launch {
            try {
                val promo = productRepository.getSellerPromo()
                if (promo.isEmpty()){
                    sellerPromo.value=null
                }else{
                    sellerPromo.value=promo
                }
            }catch (e: Exception) {
                // Handle network failures or exceptions here
                Log.e("NetworkErrorPromo", e.message, e)
            }
        }
    }
    fun editDibaca(id: Int, dibaca:String){
        apiServices.updateDibaca(id, dibaca).enqueue(object : Callback<com.example.myapplication.model.Response>{
            override fun onResponse(
                call: Call<com.example.myapplication.model.Response>,
                response: Response<com.example.myapplication.model.Response>
            ) {
                if(response.isSuccessful){
                    respon.value = response.body()
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
    fun tambahPromo(min: String, max: String,diskon:String){
        apiServices.tambahpromo(min,max,diskon).enqueue(object : Callback<com.example.myapplication.model.Response>{
            override fun onResponse(
                call: Call<com.example.myapplication.model.Response>,
                response: Response<com.example.myapplication.model.Response>
            ) {
                if(response.isSuccessful){
                    respon.value = response.body()
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

    fun editPromo(id: Int,min:String,max: String,diskon: String){
        apiServices.editPromo(id,min,max,diskon).enqueue(object : Callback<com.example.myapplication.model.Response>{
            override fun onResponse(
                call: Call<com.example.myapplication.model.Response>,
                response: Response<com.example.myapplication.model.Response>
            ) {
                if(response.isSuccessful){
                    respon.value = response.body()
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

    fun deletePromo(id: Int){
        apiServices.deletepromo(id).enqueue(object : Callback<com.example.myapplication.model.Response>{
            override fun onResponse(
                call: Call<com.example.myapplication.model.Response>,
                response: Response<com.example.myapplication.model.Response>
            ) {
                if(response.isSuccessful){
                    respon.value = response.body()
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

    fun editProductNG(
        id : RequestBody,
        namaProduk : RequestBody,
        kategoriProduk : RequestBody,
        deskripsi : RequestBody,
        stok : RequestBody,
        harga : RequestBody,
        berat : RequestBody,
    ){
        apiServices.editProdukNG(id, namaProduk, kategoriProduk, deskripsi, stok, harga, berat)
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
    fun getTotal(){
        apiServices.total_penjualan().enqueue(object : Callback<GetTotalItem>{
            override fun onResponse(call: Call<GetTotalItem>, response: Response<GetTotalItem>) {
                if (response.isSuccessful) {
                    livedataTotal.value = response.body()
                }
            }

            override fun onFailure(call: Call<GetTotalItem>, t: Throwable) {
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

    fun getRequiredHistory(id: Int, OrderID:String){
        apiServices.getRequiredHistory(id, OrderID).enqueue(object : Callback<GetRequired>{
            override fun onResponse(call: Call<GetRequired>, response: Response<GetRequired>) {
                if (response.isSuccessful){
                    livedataRequire.value = response.body()
                }
            }

            override fun onFailure(call: Call<GetRequired>, t: Throwable) {
                //
            }
        })
    }
}