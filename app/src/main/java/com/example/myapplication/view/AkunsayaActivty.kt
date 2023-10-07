package com.example.myapplication.view

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.asLiveData
import com.example.myapplication.BuildConfig
import com.example.myapplication.R
import com.example.myapplication.datastore.UserManager
import com.example.myapplication.view.buyer.NotifikasiBuyerActivity
import com.example.myapplication.view.seller.DaftarJualActivity
import com.example.myapplication.view.seller.LengkapiDetailProductActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_akunsaya_activty.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AkunsayaActivty : AppCompatActivity() {
    private lateinit var  userManager: UserManager
    private var username : String = ""


    private val bottomNavigasi = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId){
            R.id.notifikasi -> {
                Toast.makeText(this, "Kamu Sedang Berada Di Notifikasi", Toast.LENGTH_SHORT).show()
                return@OnNavigationItemSelectedListener false
            }
            R.id.history -> {
                Toast.makeText(this, "Kamu Sedang Berada Di History", Toast.LENGTH_SHORT).show()
                return@OnNavigationItemSelectedListener false
            }
            R.id.dashboard -> {
                startActivity(Intent(this, HomeActivity::class.java))
                return@OnNavigationItemSelectedListener true
            }
            R.id.jual -> {
                val booleanvalue = userManager.getBooleanValue()
                if (booleanvalue == true){
                    startActivity(Intent(this, LengkapiDetailProductActivity::class.java))
                } else {
                    Toast.makeText(applicationContext, "Anda Belum Login", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.akun -> {
                Toast.makeText(this, "Kamu Sedang Berada Di Akun", Toast.LENGTH_SHORT).show()
                return@OnNavigationItemSelectedListener false
            }
            R.id.daftar_jual -> {
                startActivity(Intent(this, DaftarJualActivity::class.java))
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_akunsaya_activty)

        userManager = UserManager(this)
        username = userManager.fetchusername().toString()
        versiapp.text = "versi : "+BuildConfig.VERSION_NAME
        val booleanvalue = userManager.getBooleanValue()
        if (booleanvalue && userManager.fetchstatus() == "seller") {
            val botnav = findViewById<BottomNavigationView>(R.id.navigation)
            val botnav2 = findViewById<BottomNavigationView>(R.id.default_navigation)
            botnav2.isInvisible = true
            botnav.setOnNavigationItemSelectedListener(bottomNavigasi)
        } else {
            val botnav = findViewById<BottomNavigationView>(R.id.default_navigation)
            val botnav2 = findViewById<BottomNavigationView>(R.id.navigation)
            botnav2.isInvisible = true
            botnav.setOnNavigationItemSelectedListener(bottomNavigasi)
        }
            if (booleanvalue == true){
                akunsaya_login.isInvisible = true
                user_akunsaya.text = "selamat datang $username"
                keluar()
            }else{
                ubahAkunSaya.isInvisible = true
                pengaturanakunsaya.isInvisible = true
                akunsaya_btnkeluar.isInvisible = true
                akunsaya_login.isVisible = true
                akunsaya_login.setOnClickListener{
                    startActivity(Intent(this@AkunsayaActivty, LoginActivity::class.java))
                    finish()
                }
            }

        keluar()
        ubahAkun()
        changePassword()
    }


    private fun keluar(){
        val dataUserManager = UserManager(this)
        akunsaya_btnkeluar.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("KONFIRMASI LOGOUT")
                .setMessage("Anda Yakin Ingin Logout ?")

                .setPositiveButton("YA"){ _: DialogInterface, _: Int ->
                    Toast.makeText(this, "Berhasil Keluar", Toast.LENGTH_SHORT).show()
                        dataUserManager.setBooleanValue(false)
                        dataUserManager.logout()
                        startActivity(Intent(this@AkunsayaActivty, HomeActivity::class.java))
                        finish()
                }
                .setNegativeButton("TIDAK"){ dialogInterface: DialogInterface, _: Int ->
                    Toast.makeText(this, "Tidak Jadi Keluar", Toast.LENGTH_SHORT).show()
                    dialogInterface.dismiss()
                }

                .setNeutralButton("NANTI"){ dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    Toast.makeText(this, "Tidak Jadi Logout", Toast.LENGTH_SHORT).show()
                }
                .show()
        }
    }

    private fun ubahAkun(){
        ubah_akun.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun changePassword(){
        pengaturanAkun.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }
    }
}