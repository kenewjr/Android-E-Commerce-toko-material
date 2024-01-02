package com.example.myapplication.view.seller

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
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
import com.example.myapplication.view.adapter.AdapterCategorty
import com.example.myapplication.view.buyer.NotifikasiBuyerActivity
import com.example.myapplication.viewmodel.ViewModelProductSeller
import com.example.myapplication.viewmodel.ViewModelUser
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_daftar_jual_category.*
import kotlinx.android.synthetic.main.activity_daftar_jual_category.cardView_productSeller
import kotlinx.android.synthetic.main.activity_daftar_jual_category.daftarHistory
import kotlinx.android.synthetic.main.activity_daftar_jual_category.daftarPengiriman
import kotlinx.android.synthetic.main.activity_daftar_jual_category.daftar_jualEdit
import kotlinx.android.synthetic.main.activity_daftar_jual_category.navigation
import kotlinx.coroutines.DelicateCoroutinesApi

@Suppress("DEPRECATION")
@AndroidEntryPoint
class DaftarJualCategory : AppCompatActivity() {
    private lateinit var adapter : AdapterCategorty
    private lateinit var  userManager: UserManager

    @DelicateCoroutinesApi
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
    @DelicateCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_jual_category)
        userManager = UserManager(this)
        if(!isOnline(this)) {
            Toast.makeText(applicationContext, "Tidak Ada Koneksi Internet", Toast.LENGTH_SHORT)
                .show()
        }
        val botnav = findViewById<BottomNavigationView>(R.id.navigation)
        navigation.selectedItemId = R.id.daftar_jual
        botnav.setOnNavigationItemSelectedListener(bottomNavigasi)

        initView()
        daftarHistory.setOnClickListener {
            startActivity(Intent(this,DaftarJualHistory::class.java))
        }
        cardView_productSeller.setOnClickListener {
            startActivity(Intent(this,DaftarJualActivity::class.java))
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
        addCtgy()
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
    private fun addCtgy(){
        btn_tambah_ctgy.setOnClickListener{
            val viewModelSellerCategory = ViewModelProvider(this)[ViewModelProductSeller::class.java]
            val dialogView = LayoutInflater.from(this).inflate(R.layout.customdialog_editctgy, null)
            val dialogbuilder = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            // Get references to views in the custom dialog
            val buttonUpdate = dialogView.findViewById<Button>(R.id.btn_editCtgy)
            val edt1 = dialogView.findViewById<EditText>(R.id.cd_edt_ctgy)
            buttonUpdate.text = "Tambahkan Category"
            edt1.hint = "Masukan Kategori"
            buttonUpdate.setOnClickListener {
                val editTextname = dialogView.findViewById<EditText>(R.id.cd_edt_ctgy)
                viewModelSellerCategory.tambahCtgy(editTextname.text.toString())
                dialogbuilder.dismiss()
                Toast.makeText(applicationContext, "Category Berhasil Di Tambahkan", Toast.LENGTH_SHORT).show()
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
            diminati_profileKota.text = it.alamat
        }
        getCategory()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getCategory(){
        val viewModelSellerCategory = ViewModelProvider(this)[ViewModelProductSeller::class.java]

        adapter = AdapterCategorty()
        rv_ctgy.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_ctgy.adapter = adapter
        viewModelSellerCategory.getSellerCategory()
        viewModelSellerCategory.sellerCategory.observe(this) {
            if (it.isNotEmpty()){
                adapter.setDataCategory(it)
                adapter.notifyDataSetChanged()
                kalaukosongHistory.isInvisible = true
            } else {
                kalaukosongHistory.isVisible = true
            }

        }
    }
}