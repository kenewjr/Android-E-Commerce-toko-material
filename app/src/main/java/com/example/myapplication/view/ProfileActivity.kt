@file:Suppress("DEPRECATION")

package com.example.myapplication.view

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.myapplication.R
import com.example.myapplication.datastore.UserManager
import com.example.myapplication.viewmodel.ViewModelUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.coroutines.DelicateCoroutinesApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@DelicateCoroutinesApi
@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {
    private lateinit var  userManager: UserManager
    private var username : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        userManager = UserManager(this)
        userManager.username.asLiveData().observe(this){
            username = it
        }
        updatedata()
        Log.e(TAG,username)
        back.setOnClickListener {
            startActivity(Intent(this,AkunsayaActivty::class.java))
        }
        getprofile()
    }
    
    private fun getprofile(){
        val viewModelDataSeller = ViewModelProvider(this)[ViewModelUser::class.java]
        viewModelDataSeller.getProfile(id = userManager.fetchId()!!.toInt())
        viewModelDataSeller.profileData.observe(this) {
            etNama_profile.setText(it.nama)
            etAlamat_profile.setText(it.alamat)
            etNohp_profile.setText(it.nohp.toString())
        }
    }
    private fun viewmodelUpdate(username: String,nama:String,nohp:String,alamat:String){
        val viewModelDataSeller = ViewModelProvider(this)[ViewModelUser::class.java]
        viewModelDataSeller.updateUser(username, nama, nohp, alamat)
        startActivity(Intent(this,AkunsayaActivty::class.java))
    }
     private fun updatedata() {
         btn_simpan_profile.setOnClickListener {
             val nama: String = etNama_profile.text.toString()
             val alamat: String = etAlamat_profile.text.toString()
             val nohp: String = etNohp_profile.text.toString()
             viewmodelUpdate(username,nama,nohp,alamat)
             }

             Toast.makeText(this, "Berhasil Update Data", Toast.LENGTH_SHORT).show()
         }
     }

