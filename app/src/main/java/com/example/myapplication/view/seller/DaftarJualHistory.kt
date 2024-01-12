@file:Suppress("DEPRECATION")

package com.example.myapplication.view.seller


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.datastore.UserManager
import com.example.myapplication.model.GetHistoryItem
import com.example.myapplication.view.AkunsayaActivty
import com.example.myapplication.view.HomeActivity
import com.example.myapplication.view.LoginActivity
import com.example.myapplication.view.adapter.AdapterTerjual
import com.example.myapplication.view.buyer.HistoryBuyerActivity
import com.example.myapplication.view.buyer.NotifikasiBuyerActivity
import com.example.myapplication.viewmodel.ViewModelProductSeller
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.opencsv.CSVWriter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_daftar_jual_history.*
import kotlinx.coroutines.DelicateCoroutinesApi
import java.io.File
import java.io.IOException

@DelicateCoroutinesApi
@AndroidEntryPoint
class DaftarJualHistory : AppCompatActivity() {
    private lateinit var adapter : AdapterTerjual
    private lateinit var  userManager: UserManager
    private val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 123
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
            }
            R.id.akun -> {
                startActivity(Intent(this, AkunsayaActivty::class.java))
                return@OnNavigationItemSelectedListener true
            }
            R.id.daftar_jual -> {
                Toast.makeText(this, "Kamu Sedang Berada Di Daftar Jual", Toast.LENGTH_SHORT).show()
                return@OnNavigationItemSelectedListener false
            }
        }
        false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_jual_history)
        userManager = UserManager(this)
        if(!isOnline(this)) {
            Toast.makeText(applicationContext, "Tidak Ada Koneksi Internet", Toast.LENGTH_SHORT)
                .show()
        }
        val botnav = findViewById<BottomNavigationView>(R.id.navigation)
        navigation.selectedItemId = R.id.daftar_jual
        botnav.setOnNavigationItemSelectedListener(bottomNavigasi)
        initView()
        daftarCtgy.setOnClickListener {
            startActivity(Intent(this, DaftarJualCategory::class.java))
        }
        daftar_jualEdit.setOnClickListener {
            getCsv()
        }
        cardView_productSeller.setOnClickListener {
            startActivity(Intent(this,DaftarJualActivity::class.java))
        }
        daftarPengiriman.setOnClickListener {
            startActivity(Intent(this,DaftarJualPengiriman::class.java))
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

    fun getCsv() {
        if (checkStoragePermission()) {
            // Permission is already granted, proceed with exporting
            performCsvExport()
        } else {
            // Permission is not granted, request it
            requestStoragePermission()
        }
    }

    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            WRITE_EXTERNAL_STORAGE_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            WRITE_EXTERNAL_STORAGE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed with exporting
                    performCsvExport()
                } else {
                    // Permission denied, handle accordingly
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun performCsvExport() {
        val viewModelProductSeller = ViewModelProvider(this)[ViewModelProductSeller::class.java]
        viewModelProductSeller.getHistory()
        viewModelProductSeller.datahistory.observe(this) {
            if (it.isNotEmpty()) {
                val statusLunasItems = it.filter { item -> item.status == "Selesai" }
                if (statusLunasItems.isNotEmpty()) {
                    exportToCsv(statusLunasItems)
                }
            }
        }
    }

    fun getDownloadDirectory(): File? {
        val state = Environment.getExternalStorageState()
        return if (Environment.MEDIA_MOUNTED == state) {
            val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadDir.exists()) {
                downloadDir.mkdirs()
            }
            downloadDir
        } else {
            null
        }
    }

    fun exportToCsv(history: List<GetHistoryItem>) {
        val downloadDir = getDownloadDirectory()
        if (downloadDir != null) {
            val filePath = File(downloadDir, "transaksi.csv")
            try {
                val writer = filePath.writer()
                val csvWriter = CSVWriter(writer)

                // Menulis header
                val headerRecord = arrayOf("No","ID", "Nama Produk", "Jumlah Produk", "Total Harga", "Tanggal Transaksi")
                csvWriter.writeNext(headerRecord)
                var No = 1
                // Menulis data
                for (person in history) {
                    val dataRecord = arrayOf(No++.toString(),person.id, person.nama_produk, person.jumlah_produk,person.total_harga,person.tgl_transaksi)
                    csvWriter.writeNext(dataRecord)
                }

                // Menutup penulisan
                csvWriter.close()
                writer.close()
                Toast.makeText(this, "Berhasil Download Di ${filePath.absolutePath}", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                Toast.makeText(this, "Error : ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "External storage not available", Toast.LENGTH_SHORT).show()
        }
    }

    fun initView(){
        val viewModelProductSeller = ViewModelProvider(this)[ViewModelProductSeller::class.java]
        viewModelProductSeller.getTotal()
        viewModelProductSeller.dataTotal.observe(this) {
            TV_totalrp.text = "Total Pendapatan : Rp."+it.total_harga
            TV_totalbrg.text = "Total Produk Yang terjual : "+it.total_produk
        }
        initRecyclerView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecyclerView(){
        userManager = UserManager(this)
        val viewModelProductSeller = ViewModelProvider(this)[ViewModelProductSeller::class.java]
        viewModelProductSeller.getHistory()
        adapter = AdapterTerjual {
            val clickedproduct = Bundle()
            clickedproduct.putSerializable("detailorder",it)
            val pindah = Intent(this, HistoryBuyerActivity::class.java)
            pindah.putExtras(clickedproduct)
            startActivity(pindah)
        }
        rv_history.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_history.adapter = adapter
        viewModelProductSeller.datahistory.observe(this) {
            if (it.isNotEmpty()) {
                val statusLunasItems = it.filter { item -> item.status == "Selesai" }
                if (statusLunasItems.isNotEmpty()) {
                    adapter.setDataOrder(statusLunasItems)
                    adapter.notifyDataSetChanged()
                    kalaukosongHistory.isInvisible = true
                } else {
                    kalaukosongHistory.isVisible = true
                }
            } else {
                kalaukosongHistory.isVisible = true
            }
        }

    }
}