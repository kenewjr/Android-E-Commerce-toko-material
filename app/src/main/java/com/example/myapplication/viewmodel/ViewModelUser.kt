package com.example.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.DataUser
import com.example.myapplication.model.GetHistoryItem
import com.example.myapplication.model.GetKomentarItem
import com.example.myapplication.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ViewModelUser@Inject constructor(api: ApiService): ViewModel() {
    private val livedatauser = MutableLiveData<DataUser>()
    private val apiService = api

    private val liveDataProfile = MutableLiveData<DataUser>()
    val profileData: LiveData<DataUser> = liveDataProfile

    private val liveDataOrder = MutableLiveData<GetHistoryItem>()
    val OrderData: LiveData<GetHistoryItem> = liveDataOrder

    private val liveDataKomentar = MutableLiveData<List<GetKomentarItem>>()
    val komentarData : LiveData<List<GetKomentarItem>> = liveDataKomentar

    fun getProfile(id : Int){
        apiService.profileuser(id).enqueue(object : Callback<DataUser>{
            override fun onResponse(call: Call<DataUser>, response: Response<DataUser>) {
                if (response.isSuccessful) {
                    liveDataProfile.value = response.body()
                } else {
                    //
                }
            }

            override fun onFailure(call: Call<DataUser>, t: Throwable) {
                //
            }
        })
    }

    fun getKomentar(id_produk: Int){
        apiService.getKomentar(id_produk).enqueue(object : Callback<List<GetKomentarItem>>{
            override fun onResponse(
                call: Call<List<GetKomentarItem>>,
                response: Response<List<GetKomentarItem>>
            ) {
                if(response.isSuccessful){
                    liveDataKomentar.value = response.body()
                }
            }

            override fun onFailure(call: Call<List<GetKomentarItem>>, t: Throwable) {
                //
            }

        })
    }
    fun getOrder(id_user:Int,id_produk:Int){
        apiService.getOrder(id_user,id_produk).enqueue(object : Callback<GetHistoryItem>{
            override fun onResponse(
                call: Call<GetHistoryItem>,
                response: Response<GetHistoryItem>
            ) {
                liveDataOrder.value = response.body()
            }

            override fun onFailure(call: Call<GetHistoryItem>, t: Throwable) {
                //
            }

        })
    }
    fun updateUser(username: String,nama:String,nohp:String,alamat:String){
    apiService.updateuser(username,nama, nohp, alamat).enqueue(object : Callback<DataUser>{
        override fun onResponse(call: Call<DataUser>, response: Response<DataUser>) {
            if(response.isSuccessful){
                livedatauser.value = response.body()
            }
        }

        override fun onFailure(call: Call<DataUser>, t: Throwable) {
            //
        }
    })

    }
}