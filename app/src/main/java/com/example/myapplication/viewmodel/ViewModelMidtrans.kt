package com.example.myapplication.viewmodel


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.GetHistoryItem
import com.example.myapplication.network.ApiClient
import com.example.myapplication.network.ApiMidtrans
import com.example.myapplication.network.ApiMidtransStatus
import com.example.myapplication.network.ApiService
import com.example.myapplication.payment.GetPaymentStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create
import javax.inject.Inject


class ViewModelMidtrans() : ViewModel() {

    private val livedatastatus = MutableLiveData<GetPaymentStatus>()
    val datastatus : LiveData<GetPaymentStatus> = livedatastatus


    fun getStatus(orderId: String){
        val apiClient = ApiMidtransStatus.getRetrofitInstance().create(ApiMidtrans::class.java)
        val call = apiClient.getStatus(orderId)

        call.enqueue(object : Callback<GetPaymentStatus>{
            override fun onResponse(
                call: Call<GetPaymentStatus>,
                response: Response<GetPaymentStatus>
            ) {
                if (response.isSuccessful){
                    livedatastatus.value = response.body()
                }else{
                    Log.e("status error",response.toString())
                }
            }

            override fun onFailure(call: Call<GetPaymentStatus>, t: Throwable) {
                Log.e("statusfailure",t.message.toString())
                t.printStackTrace()
            }

        })
    }
//    fun getStatus(orderId : String){
//        ApiMidtransStatus.instance.getStatus(orderId).enqueue(object : Callback<GetPaymentStatus>{
//            override fun onResponse(
//                call: Call<GetPaymentStatus>,
//                response: Response<GetPaymentStatus>
//            ) {
//                if (response.isSuccessful){
//                    livedatastatus.value = response.body()
//                    Log.e("status",response.body().toString())
//                }else{
//                    Log.e("status error",response.message())
//                }
//            }
//
//            override fun onFailure(call: Call<GetPaymentStatus>, t: Throwable) {
//                Log.e("statusfailure",t.message.toString())
//            }
//        })
//    }
}