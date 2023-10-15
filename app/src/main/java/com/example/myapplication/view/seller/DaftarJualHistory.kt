@file:Suppress("DEPRECATION")

package com.example.myapplication.view.seller


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.datastore.UserManager
import com.example.myapplication.view.AkunsayaActivty
import com.example.myapplication.view.HomeActivity
import com.example.myapplication.view.LoginActivity
import com.example.myapplication.view.adapter.AdapterTerjual
import com.example.myapplication.view.buyer.NotifikasiBuyerActivity
import com.example.myapplication.viewmodel.ViewModelProductSeller
import com.example.myapplication.viewmodel.ViewModelUser
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_daftar_jual_history.*
import kotlinx.android.synthetic.main.activity_daftar_jual_history.TV_nama
import kotlinx.android.synthetic.main.activity_daftar_jual_history.cardView_productSeller
import kotlinx.android.synthetic.main.activity_daftar_jual_history.daftarCtgy
import kotlinx.android.synthetic.main.activity_daftar_jual_history.daftar_jualEdit
import kotlinx.android.synthetic.main.activity_daftar_jual_history.diminati_profileKota
import kotlinx.android.synthetic.main.activity_daftar_jual_history.kalaukosongHistory
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
@AndroidEntryPoint
class DaftarJualHistory : AppCompatActivity() {
    private lateinit var adapter : AdapterTerjual
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
                if (booleanvalue == true){
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
        val botnav = findViewById<BottomNavigationView>(R.id.navigation)
        botnav.setOnNavigationItemSelectedListener(bottomNavigasi)

        daftarCtgy.setOnClickListener {
            startActivity(Intent(this, DaftarJualCategory::class.java))
        }
        daftar_jualEdit.setOnClickListener {
            startActivity(Intent(this,AkunsayaActivty::class.java))
        }
        cardView_productSeller.setOnClickListener {
            startActivity(Intent(this,DaftarJualActivity::class.java))
        }
       initView()
    }

    private fun initView(){
        val viewModelDataSeller = ViewModelProvider(this)[ViewModelUser::class.java]
        viewModelDataSeller.getProfile(id = userManager.fetchId()!!.toInt())
        viewModelDataSeller.profileData.observe(this) {
            TV_nama.text = it.nama
            diminati_profileKota.text = it.alamat
        }
        initRecyclerView()
    }

    private fun initRecyclerView(){
        userManager = UserManager(this)
        val viewModelProductSeller = ViewModelProvider(this)[ViewModelProductSeller::class.java]
        viewModelProductSeller.getHistory()
        adapter = AdapterTerjual {

        }
        rv_history.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_history.adapter = adapter
        viewModelProductSeller.datahistory.observe(this) { it ->
            if (it.isNotEmpty()) {
                val statusLunasItems = it.filter { item -> item.status == "Lunas" }
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