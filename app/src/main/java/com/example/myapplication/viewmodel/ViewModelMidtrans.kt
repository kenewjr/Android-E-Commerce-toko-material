package com.example.myapplication.viewmodel

import and5.abrar.e_commerce.repository.ProductRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.GetHistoryItem
import com.example.myapplication.network.ApiMidtrans
import com.example.myapplication.network.ApiService
import com.example.myapplication.payment.GetPaymentStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ViewModelMidtrans @Inject constructor(api: ApiMidtrans) : ViewModel() {

    private val livedatastatus = MutableLiveData<GetPaymentStatus>()
    val datastatus : LiveData<GetPaymentStatus> = livedatastatus

    private val apiMidtrans = api

    fun getStatus(orderId : String){
        apiMidtrans.getStatus(orderId).enqueue(object : Callback<GetPaymentStatus>{
            override fun onResponse(
                call: Call<GetPaymentStatus>,
                response: Response<GetPaymentStatus>
            ) {
                if (response.isSuccessful){
                    livedatastatus.value = response.body()
                }
            }

            override fun onFailure(call: Call<GetPaymentStatus>, t: Throwable) {
                //
            }
        })
    }
}