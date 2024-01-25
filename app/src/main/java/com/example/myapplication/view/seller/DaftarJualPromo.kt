package com.example.myapplication.view.seller

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.datastore.UserManager
import com.example.myapplication.view.AkunsayaActivty
import com.example.myapplication.view.HomeActivity
import com.example.myapplication.view.LoginActivity
import com.example.myapplication.view.adapter.AdapterPromo
import com.example.myapplication.view.buyer.NotifikasiBuyerActivity
import com.example.myapplication.viewmodel.ViewModelProductSeller
import com.example.myapplication.viewmodel.ViewModelUser
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_daftar_jual_promo.*
import kotlinx.android.synthetic.main.activity_daftar_jual_promo.TV_nama
import kotlinx.android.synthetic.main.activity_daftar_jual_promo.cardView_productSeller
import kotlinx.android.synthetic.main.activity_daftar_jual_promo.daftarCtgy
import kotlinx.android.synthetic.main.activity_daftar_jual_promo.daftarHistory
import kotlinx.android.synthetic.main.activity_daftar_jual_promo.daftarPengiriman
import kotlinx.android.synthetic.main.activity_daftar_jual_promo.daftar_jualEdit
import kotlinx.android.synthetic.main.activity_daftar_jual_promo.kalaukosongHistory
import kotlinx.android.synthetic.main.activity_daftar_jual_promo.navigation

@AndroidEntryPoint
class DaftarJualPromo : AppCompatActivity() {
    private lateinit var  userManager: UserManager
    private lateinit var adapter : AdapterPromo
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
        setContentView(R.layout.activity_daftar_jual_promo)
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
        addpromo()
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
        daftarPengiriman.setOnClickListener {
            startActivity(Intent(this,DaftarJualPengiriman::class.java))
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
    private fun addpromo(){
        btn_tambah_promo.setOnClickListener{
            val viewModelSeller = ViewModelProvider(this)[ViewModelProductSeller::class.java]
            val dialogView = LayoutInflater.from(this).inflate(R.layout.customdialog_promo, null)
            val dialogbuilder = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            // Get references to views in the custom dialog
            val buttonUpdate = dialogView.findViewById<Button>(R.id.btn_editPromo)
            buttonUpdate.text = "Tambahkan Promo"
            buttonUpdate.setOnClickListener {
                val editTextmin = dialogView.findViewById<EditText>(R.id.cd_min)
                val editTextmax = dialogView.findViewById<EditText>(R.id.cd_max)
                val editTextdiskon = dialogView.findViewById<EditText>(R.id.cd_diskon)

                val minVal = editTextmin.text.toString().toDoubleOrNull()
                val maxVal = editTextmax.text.toString().toDoubleOrNull()
                val diskon = editTextdiskon.text.toString().toDoubleOrNull()
                if (minVal != null && maxVal != null && diskon != null && maxVal > minVal && diskon < minVal ||
                    editTextmin.text.toString().toInt() == 0 &&  editTextmax.text.toString().toInt() == 0 && editTextdiskon.text.toString().toInt() == 0) {
                    viewModelSeller.tambahPromo(
                        editTextmin.text.toString(),
                        editTextmax.text.toString(),
                        editTextdiskon.text.toString()
                    )
                    dialogbuilder.dismiss()
                    Toast.makeText(applicationContext, "Promo Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
                    initView()
                } else if(diskon == null){
                    Toast.makeText(applicationContext, "Diskon Tidak Boleh Kosong", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(applicationContext, "Nilai di Maksimal Harga harus lebih besar dari Minimal Harga dan Diskon lebih kecil Dari Minimal Harga", Toast.LENGTH_SHORT).show()
                }
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
        val viewModelSeller = ViewModelProvider(this)[ViewModelProductSeller::class.java]

        adapter = AdapterPromo()
        rv_promo.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_promo.adapter = adapter
        viewModelSeller.getPromo()
        viewModelSeller.sellerPromo.observe(this) {
            if (it.isNotEmpty()){
                adapter.setDataPromo(it)
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