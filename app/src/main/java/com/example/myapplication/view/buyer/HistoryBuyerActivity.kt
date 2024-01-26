package com.example.myapplication.view.buyer

import android.annotation.SuppressLint
import android.content.*
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.datastore.UserManager
import com.example.myapplication.model.GetHistoryItem
import com.example.myapplication.view.AkunsayaActivty
import com.example.myapplication.view.HomeActivity
import com.example.myapplication.view.LoginActivity
import com.example.myapplication.view.seller.DaftarJualActivity
import com.example.myapplication.view.seller.LengkapiDetailProductActivity
import com.example.myapplication.viewmodel.ViewModelUser
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_history_buyer.*
import kotlinx.android.synthetic.main.activity_history_buyer.default_navigation
import kotlinx.android.synthetic.main.activity_history_buyer.navigation
import kotlinx.android.synthetic.main.activity_notifikasi_buyer.*
import kotlinx.coroutines.DelicateCoroutinesApi

@Suppress("DEPRECATION")
@AndroidEntryPoint
class HistoryBuyerActivity : AppCompatActivity() {
    private lateinit var  userManager: UserManager
    private var getstatus = ""
    @DelicateCoroutinesApi
    private val bottomNavigasi = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId){
            R.id.notifikasi -> {
                Toast.makeText(this, "Kamu Sedang Berada Di Notifikasi", Toast.LENGTH_SHORT).show()
                return@OnNavigationItemSelectedListener true
            }
            R.id.history -> {
                Toast.makeText(this, "Kamu Sedang Berada Di History", Toast.LENGTH_SHORT).show()
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

    @SuppressLint("SetTextI18n")
    @DelicateCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_buyer)
        userManager = UserManager(this)
        if(!isOnline(this)) {
            Toast.makeText(applicationContext, "Tidak Ada Koneksi Internet", Toast.LENGTH_SHORT)
                .show()
        }
        fetchnotif()
        val booleanvalue = userManager.getBooleanValue()
        navigation.selectedItemId = R.id.notifikasi
        default_navigation.selectedItemId = R.id.history
        if (booleanvalue && userManager.fetchstatus() == "seller") {
            val botnav = findViewById<BottomNavigationView>(R.id.navigation)
            val botnav2 = findViewById<BottomNavigationView>(R.id.default_navigation)
            botnav2.isInvisible = true
            botnav.setOnNavigationItemSelectedListener(bottomNavigasi)
            btn_batal.isInvisible = true
            if(getstatus == "Terkirim" || getstatus == "Selesai"||getstatus == "pending" ||getstatus == "Dibatalkan"){
                btn_selesai.isInvisible = true
            }else {
                btn_selesai.text = "Kirim Pesanan"
            }
            btnKirim()
        } else {
            val botnav = findViewById<BottomNavigationView>(R.id.default_navigation)
            val botnav2 = findViewById<BottomNavigationView>(R.id.navigation)
            botnav2.isInvisible = true
            botnav.setOnNavigationItemSelectedListener(bottomNavigasi)
            if(getstatus == "pending"||getstatus == "Lunas"){
                btn_selesai.isInvisible = true
            }else {
                btn_selesai.text = "Pesanan Selesai"
            }
            if(getstatus == "Dibatalkan" || getstatus == "Selesai"){
                btn_selesai.isInvisible = true
                btn_batal.isInvisible = true
            }
            if(getstatus == "Terkirim"){
                btn_batal.isInvisible = true
            }
            btnSelesai()
        }
        btnBatal()

    }

    private fun btnBatal(){
        btn_batal.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("KONFIRMASI BATAL")
                .setMessage("Anda Yakin Ingin Membatalkan Pembelian Produk Ini ?")
                .setPositiveButton("YA"){_:DialogInterface, _:Int ->
                    val dataProduct = intent.extras!!.getSerializable("detailorder") as GetHistoryItem?
                    val viewModel = ViewModelProvider(this)[ViewModelUser::class.java]
                    viewModel.changeStatus("Dibatalkan",dataProduct!!.id.toInt())
                    Toast.makeText(this, "Berhasil Membatalkan Produk", Toast.LENGTH_SHORT).show()
                    btn_batal.isInvisible = true
                    updateStatus("Dibatalkan")
                }
                .setNegativeButton("TIDAK") { dialogInterface: DialogInterface, _: Int ->
                    Toast.makeText(this, "Tidak Jadi Membatalkan Produk", Toast.LENGTH_SHORT).show()
                    dialogInterface.dismiss()
                }
                .show()
        }
    }

    private fun btnKirim(){
        btn_selesai.setOnClickListener{
            val dataProduct = intent.extras!!.getSerializable("detailorder") as GetHistoryItem?
            val viewModel = ViewModelProvider(this)[ViewModelUser::class.java]
            viewModel.changeStatus("Terkirim",dataProduct!!.id.toInt())
            updateStatus("Terkirim")
            btn_selesai.isInvisible = true // Hide the button
            Toast.makeText(this, "Status Pesanan Berhasil Berubah Menjadi Terkirim", Toast.LENGTH_SHORT).show()
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
    private fun btnSelesai(){
        btn_selesai.setOnClickListener {
            val dataProduct = intent.extras!!.getSerializable("detailorder") as GetHistoryItem?
            val viewModel = ViewModelProvider(this)[ViewModelUser::class.java]
            viewModel.changeStatus("Selesai",dataProduct!!.id.toInt())
            updateStatus("Selesai")
            Toast.makeText(this, "Status Pesanan Berhasil Berubah Menjadi Selesai", Toast.LENGTH_SHORT).show()
            btn_selesai.isInvisible = true // Hide the button
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateStatus(newStatus: String) {
        getstatus = newStatus
        tv_status.text = "Status : $newStatus"
        // Update any other UI components based on the new status here
    }

    private fun copyTextToClipboard(textToCopy: String) {
        val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("norek", textToCopy)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(this, "Nomor Berahasil Di Copy: $textToCopy", Toast.LENGTH_SHORT).show()
    }
    @SuppressLint("SetTextI18n")
    private fun fetchnotif(){
        val dataProduct = intent.extras!!.getSerializable("detailorder") as GetHistoryItem?
        val viewModelDataSeller = ViewModelProvider(this)[ViewModelUser::class.java]
        viewModelDataSeller.getProfile(id = dataProduct!!.id_user.toInt())
        viewModelDataSeller.profileData.observe(this) {
            historyBuyer_nohp.text = "No Hp Penerima : ${it.nohp}"
        }
        with(dataProduct){
            tv_norek.setOnClickListener {
                copyTextToClipboard(tujuan_rekening)
            }
            getstatus = status

            tv_status.text = "Status : $status"
            tv_orderid.text = "Order Id : $order_id"
            tv_tanggal.text = "Tanggal Transaksi : $tgl_transaksi"
            tv_norek.text = "Tujuan Nomor Rekening : $tujuan_rekening"
            if(nama_rekening == "70012"){
                tv_jenisbank.text = "Kode Bank : midtrans($nama_rekening)"
            }else{
                tv_jenisbank.text = "Penerima : $nama_rekening"
            }
            Glide.with(this@HistoryBuyerActivity)
                .load(gambar)
                .override(80, 80)
                .into(gambarProdukBuyer)
            historyBuyer_harga.text = "Harga Produk : Rp.$harga_produk"
            historyBuyer_namaProduk.text = "Nama Produk : $nama_produk"
            historyBuyer_order.text = "Order Id : $order_id"
            historyBuyer_alamat.text = "Alamat Pengiriman : $alamat"
            historyBuyer_totalongkos.text = "Harga Ongkir : Rp.$ongkos"
            historyBuyer_jumlahbrg.text = "Jumlah Produk : $jumlah_produk"
            historyBuyer_ttlbelanja.text = "Total Harga : Rp.$total_harga"
            historyBuyer_namaPenerima.text = "Nama Penerima : $nama_pembeli"
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}