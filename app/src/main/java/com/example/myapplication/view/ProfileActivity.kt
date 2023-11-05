@file:Suppress("DEPRECATION")

package com.example.myapplication.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.datastore.UserManager
import com.example.myapplication.viewmodel.ViewModelUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {
    private lateinit var  userManager: UserManager
    private var username : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        userManager = UserManager(this)
        username = userManager.fetchusername().toString()
        back.setOnClickListener {
            startActivity(Intent(this,AkunsayaActivty::class.java))
        }
        getprofile()
        updatedata()
    }
    
    private fun getprofile(){
        val viewModelDataSeller = ViewModelProvider(this)[ViewModelUser::class.java]
        viewModelDataSeller.getProfile(id = userManager.fetchId()!!.toInt())
        viewModelDataSeller.profileData.observe(this) {
            etNama_profile.setText(it.nama)
            etAlamat_profile.setText(it.alamat)
            etNohp_profile.setText(it.nohp)
            etkodepos_profile.setText(it.kodepos)
            etemail_profile.setText(it.email)
            etkota_profile.setText(it.kota)
        }
    }
    private fun viewmodelUpdate(username: String,nama:String,nohp:String,alamat:String,kota:String,kodepos:String,email:String){
        val viewModelDataSeller = ViewModelProvider(this)[ViewModelUser::class.java]
        viewModelDataSeller.updateUser(username, nama, nohp, alamat, kota, kodepos, email)
        startActivity(Intent(this,AkunsayaActivty::class.java))
    }
     private fun updatedata() {
         btn_simpan_profile.setOnClickListener {
             val nama: String = etNama_profile.text.toString()
             val alamat: String = etAlamat_profile.text.toString()
             val nohp: String = etNohp_profile.text.toString()
             val kodepos : String = etkodepos_profile.text.toString()
             val email : String = etemail_profile.text.toString()
             val kota : String = etkota_profile.text.toString()
             viewmodelUpdate(username,nama,nohp,alamat,kota, kodepos, email)
             Toast.makeText(this, "Berhasil Update Data", Toast.LENGTH_SHORT).show()
             }
         }
     }

