@file:Suppress("UselessCallOnNotNull", "DEPRECATION")

package com.example.myapplication.view.buyer


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.datastore.UserManager
import com.example.myapplication.model.GetAllProdukItem
import com.example.myapplication.network.ApiClient
import com.example.myapplication.payment.PaymentMidtransActivty
import com.example.myapplication.view.HomeActivity
import com.example.myapplication.view.LoginActivity
import com.example.myapplication.view.adapter.AdapterKomentar
import com.example.myapplication.viewmodel.ViewModelHome
import com.example.myapplication.viewmodel.ViewModelUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add_product_buyer.*
import kotlinx.android.synthetic.main.activity_notifikasi_buyer.*
import kotlinx.android.synthetic.main.custom_dialog_komentar.view.*
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
@AndroidEntryPoint
class AddProductBuyerActivity : AppCompatActivity() {
    private lateinit var userManager: UserManager
    private lateinit var apiClient: ApiClient
    private var datalengkap: String = "ada"
    private var nama: String = ""
    private var produkpilih: String = ""
    private var stok: Int = 0
    private lateinit var  adapterKomentar: AdapterKomentar
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product_buyer)
        userManager = UserManager(this)
        apiClient = ApiClient()
        if(!isOnline(this)) {
            Toast.makeText(applicationContext, "Tidak Ada Koneksi Internet", Toast.LENGTH_SHORT)
                .show()
        }
        back.setOnClickListener {
            startActivity(Intent(this,HomeActivity::class.java))
        }

        addProductBuyer_btnTertarik.setOnClickListener {
            val dataProduct = intent.extras!!.getSerializable("detailproduk") as GetAllProdukItem?
            val pindah = Intent(this, PaymentMidtransActivty::class.java)
            pindah.putExtra("idproduk",dataProduct!!.id)
            startActivity(pindah)
        }
        val booleanvalue = userManager.getBooleanValue()
        detailData()
        fetchkomentar()
        if (booleanvalue){
            imageKomen.setOnClickListener {
                addKomentar()
            }
            chatWA()
            disablebutton()
            checkakun()
        } else {
            imageKomen.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
                Toast.makeText(this, "Silahkan Login Terlebih Dahulu", Toast.LENGTH_SHORT).show()
            }
            addProductBuyer_btnTertarik.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
                Toast.makeText(this, "Silahkan Login Terlebih Dahulu", Toast.LENGTH_SHORT).show()
            }
            addProductBuyer_btnWA.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
                Toast.makeText(this, "Silahkan Login Terlebih Dahulu", Toast.LENGTH_SHORT).show()
            }
        }
        if (userManager.fetchstatus() == "seller"){
            imageKomen.isInvisible = true
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
    private fun chatWA(){
        addProductBuyer_btnWA.setOnClickListener {
            val dataProduct = intent.extras!!.getSerializable("detailproduk") as GetAllProdukItem?
            val message = "Haloo Mas Saya ${userManager.fetchusername()}Apakah Produk ini masih Ada? \n"+
                    "Nama Produk : ${dataProduct!!.nama} \n"+
                    "Berat Produk : ${dataProduct.berat} \n"+
                    "Harga Produk : ${dataProduct.harga} \n"

            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(
                String.format(
                    "https://api.whatsapp.com/send?phone=%s&text=%s",
                   "+6208977715400",
                    message
                )
            )))
        }
    }
    @SuppressLint("SetTextI18n")
    private fun disablebutton(){
        userManager = UserManager(this)
        val viewModel = ViewModelProvider(this)[ViewModelUser::class.java]
        viewModel.getOrder(userManager.fetchId()!!.toInt(),produkpilih.toInt())
        runOnUiThread {
            viewModel.orderData.observe(this){
                if (it.status == "pending"){
                    addProductBuyer_btnTertarik.text = "Segera Selesaikan Pembayaran"
                }else if (stok == 0) {
                    addProductBuyer_btnTertarik.text = "Barang Sedang Habis"
                }else{
                    addProductBuyer_btnTertarik.text = "Beli Sekarang"
                }
            }
        }
    }
    private fun addKomentar(){
        userManager = UserManager(this)
        val viewModelKomentar = ViewModelProvider(this)[ViewModelUser::class.java]
        val dataProduct = intent.extras!!.getSerializable("detailproduk") as GetAllProdukItem?
        val dialogView = layoutInflater.inflate(R.layout.custom_dialog_komentar, null)
        val dialogbuilder = AlertDialog.Builder(this).setView(dialogView).create()
        val btnaddkomen = dialogView.addKomentarbtn
        val ratingBar = dialogView.rBarActive
        ratingBar.rating = 5.0f
        btnaddkomen.setOnClickListener {
            val komentar = dialogView.edt_komentar.text.toString()
            val rating = ratingBar.rating
           viewModelKomentar.addKomentar(komentar,dataProduct!!.id.toInt(),userManager.fetchId()!!.toInt(),nama,rating)
            dialogbuilder.dismiss()
            Toast.makeText(this, "Berhasil Menambahkan Pesan", Toast.LENGTH_SHORT).show()
            fetchkomentar()
        }
        dialogbuilder.setCancelable(true)
        dialogbuilder.show()
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun fetchkomentar(){
        val dataProduct = intent.extras!!.getSerializable("detailproduk") as GetAllProdukItem?
        val viewModelKomentar = ViewModelProvider(this)[ViewModelUser::class.java]
        viewModelKomentar.getKomentar(dataProduct!!.id.toInt())
        adapterKomentar = AdapterKomentar()
        rv_produkkomen.layoutManager = LinearLayoutManager(this)
        rv_produkkomen.adapter = adapterKomentar
        viewModelKomentar.komentarData.observe(this){
            adapterKomentar.setKomentar(it)
            adapterKomentar.notifyDataSetChanged()
        }
    }
    private fun checkakun(){
        val viewModelDataSeller = ViewModelProvider(this)[ViewModelUser::class.java]
        viewModelDataSeller.getProfile(id = userManager.fetchId()!!.toInt())
        viewModelDataSeller.profileData.observe(this) {
            if (it.nama.isNullOrEmpty() ||
                it.alamat.isNullOrEmpty() ||
                it.nohp.isNullOrEmpty() ||
                it.password.isNullOrEmpty()
            ) {
                datalengkap = "kosong"
            }
            nama = it.nama
        }
    }
    @SuppressLint("SetTextI18n")
    private fun detailData() {
        userManager = UserManager(this)
        val dataProduct = intent.extras!!.getSerializable("detailproduk") as GetAllProdukItem?
        if (dataProduct != null) {
            val rBar = findViewById<RatingBar>(R.id.rBar)
            val viewModel = ViewModelProvider(this)[ViewModelHome::class.java]
            viewModel.getProductid(dataProduct.id.toInt())
            val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
            Glide.with(this)
                .load(dataProduct.gambar)
                .override(400, 350)
                .apply(requestOptions)
                .into(tv_imgdetailproduct)
            produkpilih = dataProduct.id
            stok = dataProduct.stok.toInt()
            if (dataProduct.ratinguser.toInt() >=1){
                val jumlah = dataProduct.rating / dataProduct.ratinguser.toInt()
                val ratingWithF = "$jumlah" + "f"
                rBar.rating = ratingWithF.toFloat()
            }else{
                rBar.rating = 0f
            }
            tv_stok.text = "Stok : "+dataProduct.stok
            tv_judulproductdetail.text = "Nama Produk : "+dataProduct.nama
            tv_acesorisproductdetail.text ="Kategori : "+ dataProduct.kategori
            tv_hargaproductdetail.text = "Harga Produk : Rp."+dataProduct.harga
            tv_beratproduk.text = "Berat Produk : "+dataProduct.berat +"kg"
            tv_deskripsidetail.text = dataProduct.deskripsi


        }
    }
}