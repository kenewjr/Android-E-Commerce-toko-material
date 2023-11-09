package com.example.myapplication.view

import android.annotation.SuppressLint
import android.content.*
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.BuildConfig
import com.example.myapplication.R
import com.example.myapplication.datastore.UserManager
import com.example.myapplication.view.buyer.NotifikasiBuyerActivity
import com.example.myapplication.view.seller.DaftarJualActivity
import com.example.myapplication.view.seller.LengkapiDetailProductActivity
import com.example.myapplication.viewmodel.ViewModelUser
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_akunsaya_activty.*
import kotlinx.android.synthetic.main.activity_akunsaya_activty.default_navigation
import kotlinx.android.synthetic.main.activity_akunsaya_activty.navigation
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.DelicateCoroutinesApi

@OptIn(DelicateCoroutinesApi::class)
@Suppress("DEPRECATION")
@AndroidEntryPoint
class AkunsayaActivty : AppCompatActivity() {
    private lateinit var  userManager: UserManager
    private var username : String = ""

    private val bottomNavigasi = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when(item.itemId){
            R.id.notifikasi -> {
                startActivity(Intent(this, NotifikasiBuyerActivity::class.java))
                return@OnNavigationItemSelectedListener true
            }
            R.id.history -> {
                val booleanvalue = userManager.getBooleanValue()
                if (booleanvalue) {
                    startActivity(Intent(this, NotifikasiBuyerActivity::class.java))
                } else {
                    Toast.makeText(applicationContext, "Anda Belum Login", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.home -> {
                startActivity(Intent(this, HomeActivity::class.java))
                return@OnNavigationItemSelectedListener true
            }
            R.id.jual -> {
                val booleanvalue = userManager.getBooleanValue()
                if (booleanvalue){
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

    @SuppressLint("SuspiciousIndentation", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_akunsaya_activty)
        userManager = UserManager(this)
        username = userManager.fetchusername().toString()
        versiapp.text = "versi : "+BuildConfig.VERSION_NAME
        tvusername.text = ""
        tvpassword.text = ""
        val booleanvalue = userManager.getBooleanValue()
        if(!isOnline(this)) {
            Toast.makeText(applicationContext, "Tidak Ada Koneksi Internet", Toast.LENGTH_SHORT)
                .show()
        }
        navigation.selectedItemId = R.id.akun
        default_navigation.selectedItemId = R.id.akun

        if (booleanvalue && userManager.fetchstatus() == "seller") {
            val botnav = findViewById<BottomNavigationView>(R.id.navigation)
            val botnav2 = findViewById<BottomNavigationView>(R.id.default_navigation)
            botnav2.isInvisible = true
            botnav.setOnNavigationItemSelectedListener(bottomNavigasi)
            resetpassword.setOnClickListener {
                resetPassword()
            }
        } else {
            val botnav = findViewById<BottomNavigationView>(R.id.default_navigation)
            val botnav2 = findViewById<BottomNavigationView>(R.id.navigation)
            botnav2.isInvisible = true
            resetpassword.isInvisible = true
            tvusername.isInvisible = true
            tvpassword.isInvisible = true
            botnav.setOnNavigationItemSelectedListener(bottomNavigasi)
        }
            if (booleanvalue){
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

    private fun copyTextToClipboard(textToCopy: String) {
        val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Data Akun", textToCopy)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(this, "Akun Berahasil Di Copy: $textToCopy", Toast.LENGTH_SHORT).show()
    }
    @SuppressLint("SetTextI18n")
    private fun resetPassword() {
        val viewModelAkun = ViewModelProvider(this)[ViewModelUser::class.java]
        val dialogView = LayoutInflater.from(this).inflate(R.layout.customdialog_editctgy, null)
        val dialogbuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Get references to views in the custom dialog
        val buttonUpdate = dialogView.findViewById<Button>(R.id.btn_editCtgy)
        val tv1 = dialogView.findViewById<TextView>(R.id.tv_komentar)
        val edt1 = dialogView.findViewById<EditText>(R.id.cd_edt_ctgy)
        tv1.text = "Reset Password"
        edt1.hint = "Masukan NoHP/Email/Username Pengguna"
        buttonUpdate.text = "Reset Password"
        buttonUpdate.setOnClickListener {
            val editTextname = dialogView.findViewById<EditText>(R.id.cd_edt_ctgy)
            viewModelAkun.lupaPassword(editTextname.text.toString())
            dialogbuilder.dismiss()
            viewModelAkun.passwordData.observe(this){
                tvusername.text ="Username User : "+ it.username
                tvpassword.text = "Password Baru : "+it.new_password
                copyTextToClipboard("Username : " + it.username +"\n"+
                                      "Password : "+it.new_password)
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        }
        // Show the custom dialog
        dialogbuilder.setCancelable(true)
        dialogbuilder.show()
    }
    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        } else {
            TODO("VERSION.SDK_INT < M")
        }
        if (capabilities != null) {
            return if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                true
            } else capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        }
        return false
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