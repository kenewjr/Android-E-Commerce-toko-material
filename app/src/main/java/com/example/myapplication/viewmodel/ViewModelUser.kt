package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.DataUser
import com.example.myapplication.model.GetHistoryItem
import com.example.myapplication.model.GetKomentarItem
import com.example.myapplication.model.ResponseLupaPasswordItem
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

    private val livedatastatus  = MutableLiveData<com.example.myapplication.model.Response>()

    private val liveDataProfile = MutableLiveData<DataUser>()
    val profileData: LiveData<DataUser> = liveDataProfile

    private val liveDataOrder = MutableLiveData<GetHistoryItem>()
    val orderData: LiveData<GetHistoryItem> = liveDataOrder

    private val liveDataPassword = MutableLiveData<ResponseLupaPasswordItem>()
    val passwordData: LiveData<ResponseLupaPasswordItem> = liveDataPassword

    private val liveDataKomentar = MutableLiveData<List<GetKomentarItem>>()
    val komentarData : LiveData<List<GetKomentarItem>> = liveDataKomentar

    private val liveKomentar= MutableLiveData<com.example.myapplication.model.Response>()

    fun lupaPasswordEmail(password: String){
        apiService.lupaPasswordEmail(password).enqueue(object : Callback<ResponseLupaPasswordItem>{
            override fun onResponse(
                call: Call<ResponseLupaPasswordItem>,
                response: Response<ResponseLupaPasswordItem>
            ) {
                if (response.isSuccessful){
                    liveDataPassword.value = response.body()
                }else{
                    liveDataPassword.value = response.body()
                }
            }

            override fun onFailure(call: Call<ResponseLupaPasswordItem>, t: Throwable) {
                Log.e("vmlupapass", t.message.toString())
            }

        })
    }
    fun lupaPassword(password: String){
        apiService.LupaPassword(password).enqueue(object : Callback<ResponseLupaPasswordItem>{
            override fun onResponse(
                call: Call<ResponseLupaPasswordItem>,
                response: Response<ResponseLupaPasswordItem>
            ) {
                if (response.isSuccessful){
                    liveDataPassword.value = response.body()
                }else{
                    liveDataPassword.value = response.body()
                }
            }

            override fun onFailure(call: Call<ResponseLupaPasswordItem>, t: Throwable) {
                Log.e("vmlupapass", t.message.toString())
            }

        })
    }
    fun changeStatus(status: String,id: Int){
        apiService.updatehisotryStatus(id, status).enqueue(object : Callback<com.example.myapplication.model.Response>{
            override fun onResponse(
                call: Call<com.example.myapplication.model.Response>,
                response: Response<com.example.myapplication.model.Response>
            ) {
                if (response.isSuccessful){
                    livedatastatus.value = response.body()
                } else {
                    livedatastatus.value = response.body()
                }
            }

            override fun onFailure(
                call: Call<com.example.myapplication.model.Response>,
                t: Throwable
            ) {
                Log.e("vmchangestatus", t.message.toString())
            }

        })
    }
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
    fun addKomentar(komentar:String,id_produk: Int,id_user: Int,nama: String,rating : Float){
        apiService.tambahKomentar(id_user,komentar,nama,id_produk,rating).enqueue(object : Callback<com.example.myapplication.model.Response>{
            override fun onResponse(
                call: Call<com.example.myapplication.model.Response>,
                response: Response<com.example.myapplication.model.Response>
            ) {
                if (response.isSuccessful) {
                    liveKomentar.value = response.body()
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
                if(response.isSuccessful){
                    liveDataOrder.value = response.body()
                }
            }

            override fun onFailure(call: Call<GetHistoryItem>, t: Throwable) {
                //
            }

        })
    }
    fun updateUser(username: String,nama:String,nohp:String,alamat:String,kota:String,kodepos:String,email:String){
        apiService.updateuser(username,nama, nohp, alamat,kota, kodepos, email).enqueue(object : Callback<DataUser>{
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