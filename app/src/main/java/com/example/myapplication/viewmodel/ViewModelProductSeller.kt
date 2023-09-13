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

    private val getProduct = MutableLiveData<GetDataProductSellerItemItem>()
    fun getProductLiveData(): LiveData<GetDataProductSellerItemItem> {
        return getProduct
    }

    private val apiServices = api
    fun getSellerCategory(){
        viewModelScope.launch {
            val category = productRepository.getSellerCategory()
            sellerCategory.value = category
        }
    }

    fun getProductid(id : Int){
        apiServices.getprodukbyid(id).enqueue(object : Callback<GetDataProductSellerItemItem>{
            override fun onResponse(
                call: Call<GetDataProductSellerItemItem>,
                response: Response<GetDataProductSellerItemItem>
            ) {
                if(response.isSuccessful){
                    getProduct.value = response.body()
                    Log.e("vmps",response.body().toString())
                }
            }

            override fun onFailure(call: Call<GetDataProductSellerItemItem>, t: Throwable) {
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