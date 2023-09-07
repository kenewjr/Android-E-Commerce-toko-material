package com.example.myapplication.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.GetAllProdukItem
import com.example.myapplication.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ViewModelHome @Inject constructor(apiService: ApiService): ViewModel() {
    private var liveDataProduct = MutableLiveData<List<GetAllProdukItem>>()
    val product : LiveData<List<GetAllProdukItem>> = liveDataProduct

    private val apiServices = apiService

    init {
        viewModelScope.launch {
            val dataproduct = apiService.getallproduk()
            delay(2000)
            liveDataProduct.value = dataproduct
        }
    }
    fun searchproduct(search : String){
        apiServices.searchproduk(search).enqueue(object :
            Callback<List<GetAllProdukItem>> {
            @SuppressLint("NullSafeMutableLiveData")
            override fun onResponse(
                call: Call<List<GetAllProdukItem>>,
                response: Response<List<GetAllProdukItem>>
            ) {
                if (response.isSuccessful){
                    liveDataProduct.value = response.body()
                }else if(response.body().isNullOrEmpty()){
                    liveDataProduct.value = null
                }
            }
            @SuppressLint("NullSafeMutableLiveData")
            override fun onFailure(call: Call<List<GetAllProdukItem>>, t: Throwable) {
                liveDataProduct.value = null
            }
        })
    }

    fun Context?.toast(@StringRes textId: Int, duration: Int = Toast.LENGTH_SHORT) =
        this?.let { Toast.makeText(it, textId, duration).show() }
}