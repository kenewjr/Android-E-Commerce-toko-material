package com.example.myapplication.view.buyer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.datastore.UserManager
import com.example.myapplication.model.GetHistoryItem
import com.example.myapplication.view.AkunsayaActivty
import com.example.myapplication.view.HomeActivity
import com.example.myapplication.view.LoginActivity
import com.example.myapplication.view.seller.DaftarJualActivity
import com.example.myapplication.view.seller.LengkapiDetailProductActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_history_buyer.*
import kotlinx.coroutines.DelicateCoroutinesApi

@Suppress("DEPRECATION")
class HistoryBuyerActivity : AppCompatActivity() {
    private lateinit var  userManager: UserManager
    @DelicateCoroutinesApi
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
                return@OnNavigationItemSelectedListener false
            }
            R.id.daftar_jual -> {
                startActivity(Intent(this, DaftarJualActivity::class.java))
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
    @DelicateCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_buyer)
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
        fetchnotif()
    }

    @SuppressLint("SetTextI18n")
    private fun fetchnotif(){
        val dataProduct = intent.extras!!.getSerializable("detailorder") as GetHistoryItem?
        with(dataProduct!!){
            tv_status.text = "Status : $status"
            tv_orderid.text = "Order Id : $order_id"
            tv_tanggal.text = "Tanggal Transaksi : $tgl_transaksi"
            Glide.with(this@HistoryBuyerActivity)
                .load(gambar)
                .override(80, 80)
                .into(gambarProdukBuyer)
            historyBuyer_harga.text = "Harga Produk : $harga_produk"
            historyBuyer_namaProduk.text = "Nama Produk : $nama_produk"
            historyBuyer_order.text = "Order Id : $order_id"
            historyBuyer_alamat.text = "Alamat Pengiriman : $alamat"
            historyBuyer_totalongkos.text = "Harga Ongkir : $ongkos"
            historyBuyer_jumlahbrg.text = "Jumlah Produk : $jumlah_produk"
            historyBuyer_ttlbelanja.text = "Total Harga :$total_harga"
        }

    }
}