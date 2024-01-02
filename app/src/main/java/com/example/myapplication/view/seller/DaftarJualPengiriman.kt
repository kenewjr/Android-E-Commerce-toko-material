@file:OptIn(DelicateCoroutinesApi::class)

package com.example.myapplication.view.seller

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.datastore.UserManager
import com.example.myapplication.view.AkunsayaActivty
import com.example.myapplication.view.HomeActivity
import com.example.myapplication.view.LoginActivity
import com.example.myapplication.view.adapter.AdapterPengiriman
import com.example.myapplication.view.buyer.NotifikasiBuyerActivity
import com.example.myapplication.viewmodel.ViewModelProductSeller
import com.example.myapplication.viewmodel.ViewModelUser
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_daftar_jual_pengiriman.*
import kotlinx.android.synthetic.main.activity_daftar_jual_pengiriman.TV_nama
import kotlinx.android.synthetic.main.activity_daftar_jual_pengiriman.cardView_productSeller
import kotlinx.android.synthetic.main.activity_daftar_jual_pengiriman.daftarCtgy
import kotlinx.android.synthetic.main.activity_daftar_jual_pengiriman.daftarHistory
import kotlinx.android.synthetic.main.activity_daftar_jual_pengiriman.daftar_jualEdit
import kotlinx.android.synthetic.main.activity_daftar_jual_pengiriman.kalaukosongHistory
import kotlinx.coroutines.DelicateCoroutinesApi

@Suppress("DEPRECATION")
@AndroidEntryPoint
class DaftarJualPengiriman : AppCompatActivity() {

    private lateinit var adapter : AdapterPengiriman
    private lateinit var  userManager: UserManager

    private val bottomNavigasi = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId){
            R.id.notifikasi -> {
                startActivity(Intent(this, NotifikasiBuyerActivity::class.java))
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
                    return@OnNavigationItemSelectedListener true
                } else {
                    Toast.makeText(applicationContext, "Anda Belum Login", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
            R.id.akun -> {
                startActivity(Intent(this, AkunsayaActivty::class.java))
                return@OnNavigationItemSelectedListener true
            }
            R.id.daftar_jual -> {
                startActivity(Intent(this, DaftarJualActivity::class.java))
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_jual_pengiriman)
        userManager = UserManager(this)
        if(!isOnline(this)) {
            Toast.makeText(applicationContext, "Tidak Ada Koneksi Internet", Toast.LENGTH_SHORT)
                .show()
        }
        val botnav = findViewById<BottomNavigationView>(R.id.navigation)
        navigation.selectedItemId = R.id.daftar_jual
        botnav.setOnNavigationItemSelectedListener(bottomNavigasi)
        initView()
        editSeller()
        addpengiriman()
        cardView_productSeller.setOnClickListener {
            startActivity(Intent(this,DaftarJualActivity::class.java))
        }
        daftarCtgy.setOnClickListener{
            startActivity(Intent(this,DaftarJualCategory::class.java))
        }
        daftarHistory.setOnClickListener {
            startActivity(Intent(this,DaftarJualHistory::class.java))
        }
        daftar_jualEdit.setOnClickListener {
            startActivity(Intent(this,AkunsayaActivty::class.java))
        }
        daftarPromo.setOnClickListener {
            startActivity(Intent(this,DaftarJualPromo::class.java))
        }
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
    @SuppressLint("SetTextI18n")
    private fun addpengiriman(){
        btn_tambah_pengiriman.setOnClickListener{
            val viewModelSeller = ViewModelProvider(this)[ViewModelProductSeller::class.java]
            val dialogView = LayoutInflater.from(this).inflate(R.layout.customdialog_pengiriman, null)
            val dialogbuilder = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            // Get references to views in the custom dialog
            val buttonUpdate = dialogView.findViewById<Button>(R.id.btn_editPengiriman)
            buttonUpdate.text = "Tambahkan Pengiriman"
            buttonUpdate.setOnClickListener {
                val etKendaraan = dialogView.findViewById<EditText>(R.id.cd_kendaraan)
                val etHarga = dialogView.findViewById<EditText>(R.id.cd_harga)
                val etberat = dialogView.findViewById<EditText>(R.id.cd_berat)
                viewModelSeller.tambahPengiriman(etKendaraan.text.toString(),etHarga.text.toString(),etberat.text.toString())
                dialogbuilder.dismiss()
                Toast.makeText(applicationContext, "Pengiriman Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
                initView()
            }
            // Show the custom dialog
            dialogbuilder.setCancelable(true)
            dialogbuilder.show()
        }
    }
    fun initView(){
        val viewModelDataSeller = ViewModelProvider(this)[ViewModelUser::class.java]
        viewModelDataSeller.getProfile(id = userManager.fetchId()!!.toInt())
        viewModelDataSeller.profileData.observe(this) {
            TV_nama.text = it.nama
            profileKota.text = it.alamat
        }
        initRecyclerView()
    }

     @SuppressLint("NotifyDataSetChanged")
     fun initRecyclerView(){
        userManager = UserManager(this)
        val viewModelSellerCategory = ViewModelProvider(this)[ViewModelProductSeller::class.java]

        adapter = AdapterPengiriman()
        rv_pengiriman.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_pengiriman.adapter = adapter
        viewModelSellerCategory.getSellerPengiriman()
        viewModelSellerCategory.sellerPengiriman.observe(this) {
            if (it.isNotEmpty()){
                adapter.setDataPengiriman(it)
                adapter.notifyDataSetChanged()
                kalaukosongHistory.isInvisible = true
            }else{
                kalaukosongHistory.isVisible = true
            }
        }
    }

    private fun editSeller(){
        daftar_jualEdit.setOnClickListener {
            startActivity(Intent(this, AkunsayaActivty::class.java))
        }
    }
}