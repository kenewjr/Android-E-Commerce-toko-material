package com.example.myapplication.view

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.datastore.UserManager
import com.example.myapplication.view.adapter.AdapterHome
import com.example.myapplication.view.buyer.AddProductBuyerActivity
import com.example.myapplication.view.buyer.HistoryBuyerActivity
import com.example.myapplication.view.buyer.NotifikasiBuyerActivity
import com.example.myapplication.view.seller.DaftarJualActivity
import com.example.myapplication.view.seller.LengkapiDetailProductActivity
import com.example.myapplication.viewmodel.ViewModelHome
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var  userManager: UserManager
    private lateinit var  adapterHome: AdapterHome

    private val bottomNavigasi = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId){
            R.id.notifikasi -> {
                startActivity(Intent(this, NotifikasiBuyerActivity::class.java))
                return@OnNavigationItemSelectedListener true
            }
            R.id.history -> {
                val booleanvalue = userManager.getBooleanValue()
                if (booleanvalue == true) {
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
                    if (booleanvalue == true){
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
        setContentView(R.layout.activity_home)
        userManager = UserManager(this)
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

        if (isOnline(this)) {
            search()
            iniviewmodel()
        } else {
            Toast.makeText(applicationContext, "Tidak Ada Koneksi Internet", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun iniviewmodel() {
        adapterHome = AdapterHome {
            val clickedProduct = Bundle()
            clickedProduct.putSerializable("detailproduk", it)
            val pindah = Intent(this, AddProductBuyerActivity::class.java)
            pindah.putExtras(clickedProduct)
            startActivity(pindah)
        }
        rv_homeProduk.layoutManager = GridLayoutManager(this, 2)
        rv_homeProduk.setHasFixedSize(true)
        rv_homeProduk.setItemViewCacheSize(20)
        rv_homeProduk.isDrawingCacheEnabled = true
        rv_homeProduk.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        adapterHome.setHasStableIds(true)
        rv_homeProduk.adapter = adapterHome
        val viewModel = ViewModelProvider(this)[ViewModelHome::class.java]
        viewModel.product.observe(this) {
            if (it != null) {
                runOnUiThread {
                    adapterHome.setProduk(it)
                    adapterHome.notifyDataSetChanged()
                }
            }else {

            }
        }
    }

    fun isOnline(context: Context): Boolean {
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

    private fun search(){
        rv_homeProduk.setHasFixedSize(true)
        rv_homeProduk.setItemViewCacheSize(20)
        rv_homeProduk.isDrawingCacheEnabled = true
        rv_homeProduk.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        val viewModel = ViewModelProvider(this)[ViewModelHome::class.java]
        runOnUiThread{
            Handler(Looper.getMainLooper()).postDelayed({
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                    androidx.appcompat.widget.SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        viewModel.searchproduct(query!!)
                        return false
                    }
                    override fun onQueryTextChange(p0: String?): Boolean {
                        return false
                    }
                })
            },3000)
            viewModel.product.observe(this@HomeActivity) {
                if (it != null) {
                    adapterHome.setProduk(it)
                    adapterHome.notifyDataSetChanged()
                }else {
                    Toast.makeText(this, "Produk Yang Kamu Cari Tidak Ada", Toast.LENGTH_SHORT).show()
                }
                adapterHome = AdapterHome{
                    val pindahdata = Intent(applicationContext, AddProductBuyerActivity::class.java)
                    pindahdata.putExtra("detailproduk",it)
                    startActivity(pindahdata)
                }
                rv_homeProduk.layoutManager=GridLayoutManager(this,2)
                rv_homeProduk.adapter=adapterHome
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }
}