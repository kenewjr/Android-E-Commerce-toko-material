package com.example.myapplication.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.RelativeLayout
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.datastore.UserManager
import com.example.myapplication.view.adapter.AdapterHome
import com.example.myapplication.view.adapter.AdapterHomeCategory
import com.example.myapplication.view.buyer.AddProductBuyerActivity
import com.example.myapplication.view.buyer.NotifikasiBuyerActivity
import com.example.myapplication.view.seller.DaftarJualActivity
import com.example.myapplication.view.seller.LengkapiDetailProductActivity
import com.example.myapplication.viewmodel.ViewModelHome
import com.example.myapplication.viewmodel.ViewModelProductSeller
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_daftar_jual_history.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.default_navigation
import kotlinx.android.synthetic.main.activity_home.kalaukosongHistory
import kotlinx.android.synthetic.main.activity_home.navigation
import kotlinx.android.synthetic.main.item_product_homectgy.view.*
import kotlinx.coroutines.DelicateCoroutinesApi

@OptIn(DelicateCoroutinesApi::class)
@Suppress("DEPRECATION", "RedundantLambdaArrow", "RedundantLambdaArrow")
@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var userManager: UserManager
    private lateinit var adapterHome: AdapterHome
    private lateinit var adapterHomeCategory: AdapterHomeCategory
    private var backPressedCounter = 0

    private val bottomNavigasi = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when (item.itemId) {
            R.id.notifikasi -> {
                startActivity(Intent(this, NotifikasiBuyerActivity::class.java))
                return@OnNavigationItemSelectedListener true
            }
            R.id.history -> {
                val booleanvalue = userManager.getBooleanValue()
                if (booleanvalue) {
                    startActivity(Intent(this, NotifikasiBuyerActivity::class.java))
                } else {
                    Toast.makeText(applicationContext, "Anda Belum Login", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.home -> {
                startActivity(Intent(this, HomeActivity::class.java))
                overridePendingTransition(0,0)
                return@OnNavigationItemSelectedListener true
            }
            R.id.jual -> {
                val booleanvalue = userManager.getBooleanValue()
                if (booleanvalue) {
                    startActivity(Intent(this, LengkapiDetailProductActivity::class.java))
                } else {
                    Toast.makeText(applicationContext, "Anda Belum Login", Toast.LENGTH_SHORT)
                        .show()
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
        setContentView(R.layout.activity_home)
        userManager = UserManager(this)
        val booleanvalue = userManager.getBooleanValue()
        navigation.selectedItemId = R.id.home
        default_navigation.selectedItemId = R.id.home
        val botnav = findViewById<BottomNavigationView>(R.id.navigation)
        val botnav2 = findViewById<BottomNavigationView>(R.id.default_navigation)
        if (booleanvalue && userManager.fetchstatus() == "seller") {
            botnav2.isInvisible = true
            botnav.setOnNavigationItemSelectedListener(bottomNavigasi)
        } else {
            botnav.isInvisible = true
            botnav2.setOnNavigationItemSelectedListener(bottomNavigasi)
        }
        if (isOnline(this)) {
            search()
            iniviewmodel()
            vmCategory()
            reset()
        } else {
            Toast.makeText(applicationContext, "Tidak Ada Koneksi Internet", Toast.LENGTH_SHORT)
                .show()
        }
    }
    fun reset() {
        cv_reset.setOnClickListener{
            adapterHomeCategory.setRecyclerViewEnabled(false)
            iniviewmodel()
        }
    }

    fun enable() {
        // Call this function to enable interactions
        adapterHomeCategory.setRecyclerViewEnabled(true)
        cv_reset.setBackgroundColor(ContextCompat.getColor(this, R.color.darker))
        tv_reset.setTextColor(ContextCompat.getColor(this, R.color.white))
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun vmCategory() {
        val viewModel = ViewModelProvider(this)[ViewModelHome::class.java]
        val viewModelCategory = ViewModelProvider(this)[ViewModelProductSeller::class.java]

        adapterHomeCategory = AdapterHomeCategory { t ->
            viewModel.getCategory(t.id)
            enable()
            // Hapus observasi sebelum mengamati data kategori
            viewModel.category.removeObservers(this@HomeActivity)
            viewModel.category.observe(this@HomeActivity) { it ->
                if (it != null) {
                    adapterHome.setProduk(it)
                    adapterHome.notifyDataSetChanged()
                    kalaukosongHistory.isInvisible = true
                } else {
                    adapterHome.clearProduk()
                    kalaukosongHistory.isVisible = true
                }
            }
        }

        rv_homeProduk.layoutManager = GridLayoutManager(this, 2)
        rv_homeProduk.adapter = adapterHome

        rv_homeCategory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_homeCategory.adapter = adapterHomeCategory

        viewModelCategory.getSellerCategory()

        // Hapus observasi sebelum mengamati data kategori penjual
        viewModelCategory.sellerCategory.removeObservers(this@HomeActivity)
        viewModelCategory.sellerCategory.observe(this@HomeActivity) { it ->
            if (it != null) {
                adapterHomeCategory.setDataCategory(it)
                adapterHomeCategory.notifyDataSetChanged()
                kalaukosongHistory.isInvisible = true
            }else{
                kalaukosongHistory.isVisible = true
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun iniviewmodel() {
        adapterHome = AdapterHome {
            val clickedProduct = Bundle()
            clickedProduct.putSerializable("detailproduk", it)
            val pindah = Intent(this, AddProductBuyerActivity::class.java)
            pindah.putExtras(clickedProduct)
            startActivity(pindah)
        }
        rv_homeProduk.layoutManager = GridLayoutManager(this, 2)
        rv_homeProduk.adapter = adapterHome
        val viewModel = ViewModelProvider(this)[ViewModelHome::class.java]
        viewModel.product.observe(this) {
            if (it != null) {
                    adapterHome.setProduk(it)
                    adapterHome.notifyDataSetChanged()
                kalaukosongHistory.isInvisible = true
            } else {
                adapterHome.clearProduk()
                kalaukosongHistory.isVisible = true
            }
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

    @SuppressLint("NotifyDataSetChanged")
    private fun search() {
             val viewModel = ViewModelProvider(this@HomeActivity)[ViewModelHome::class.java]
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
            }, 3000)
        iniviewmodel()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (backPressedCounter < 1 ) {
            backPressedCounter++
            Toast.makeText(this, "Tekan Sekali Lagi Untuk Keluar", Toast.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).postDelayed({
                backPressedCounter = 0 // Reset the counter after a delay
            }, 2000) // Reset counter after 2 seconds
        } else {
            // Close the app when the back is pressed twice
            super.onBackPressed()
            finishAffinity()
        }
    }
}