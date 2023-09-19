package com.example.myapplication.viewmodel

import and5.abrar.e_commerce.repository.ProductRepository
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.GetAllProdukItem
import com.example.myapplication.model.GetCategorySellerItem
import com.example.myapplication.model.GetDataProductSellerItemItem
import com.example.myapplication.model.PostSellerProduct
import com.example.myapplication.network.ApiService
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

    private val livedataJualProduct = MutableLiveData<PostSellerProduct>()

    private val deleteProduct = MutableLiveData<com.example.myapplication.model.Response>()

    private val apiServices = api
    fun getSellerCategory(){
        viewModelScope.launch {
            val category = productRepository.getSellerCategory()
            sellerCategory.value = category
        }
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
        tglTransaksi : String,
        NamaProduk : String,
        hargaProduk : String,
        totalHarga : String,
        jumlahProduk : String,
        gambar : String
    ){

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