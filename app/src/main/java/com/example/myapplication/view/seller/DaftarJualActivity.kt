@file:Suppress("RemoveEmptyParenthesesFromLambdaCall", "DEPRECATION")

package com.example.myapplication.view.seller


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.datastore.UserManager
import com.example.myapplication.view.AkunsayaActivty
import com.example.myapplication.view.HomeActivity
import com.example.myapplication.view.LoginActivity
import com.example.myapplication.view.adapter.AdapterProductSeller
import com.example.myapplication.view.buyer.NotifikasiBuyerActivity
import com.example.myapplication.viewmodel.ViewModelHome
import com.example.myapplication.viewmodel.ViewModelUser
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_daftar_jual_seller.*
import kotlinx.android.synthetic.main.activity_daftar_jual_seller.navigation
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
@AndroidEntryPoint
class DaftarJualActivity : AppCompatActivity() {
    private lateinit var adapter : AdapterProductSeller
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
                } else {
                    Toast.makeText(applicationContext, "Anda Belum Login", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                return@OnNavigationItemSelectedListener true
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
        setContentView(R.layout.activity_daftar_jual_seller)

        userManager = UserManager(this)
        if(!isOnline(this)) {
            Toast.makeText(applicationContext, "Tidak Ada Koneksi Internet", Toast.LENGTH_SHORT)
                .show()
        }else {
            initView()
            editSeller()
            addProduct()
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
        daftarPengiriman.setOnClickListener {
            startActivity(Intent(this,DaftarJualPengiriman::class.java))
        }
        daftarPromo.setOnClickListener {
            startActivity(Intent(this,DaftarJualPromo::class.java))
        }
        navigation.selectedItemId = R.id.daftar_jual
        navigation.setOnNavigationItemSelectedListener(bottomNavigasi)
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
     fun initView(){
         val viewModelDataSeller = ViewModelProvider(this)[ViewModelUser::class.java]
         viewModelDataSeller.getProfile(id = userManager.fetchId()!!.toInt())
         viewModelDataSeller.profileData.observe(this) {
             TV_nama_product.text = it.nama
             TV_kota_product.text = it.alamat
         }
         initRecyclerView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecyclerView(){
        userManager = UserManager(this)
        val viewModel = ViewModelProvider(this)[ViewModelHome::class.java]

        adapter = AdapterProductSeller(){
            val clickedproduct = Bundle()
            clickedproduct.putSerializable("detailorder",it)
            val pindah = Intent(this,EditProduct::class.java)
            pindah.putExtras(clickedproduct)
            startActivity(pindah)
        }
        rvProductSeller.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvProductSeller.adapter = adapter

        viewModel.product.observe(this) {
            if (it.isNotEmpty()){
                adapter.setDataProductSeller(it)
                adapter.notifyDataSetChanged()
            }else{
                Toast.makeText(this, "Produk Anda Kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun editSeller(){
        daftar_jualEdit.setOnClickListener {
            startActivity(Intent(this, AkunsayaActivty::class.java))
        }
    }
    private fun addProduct(){
        image_tambah_produk.setOnClickListener {
            startActivity(Intent(this, LengkapiDetailProductActivity::class.java))
        }
    }
}