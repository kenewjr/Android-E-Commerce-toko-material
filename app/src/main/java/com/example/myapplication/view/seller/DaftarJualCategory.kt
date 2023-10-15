package com.example.myapplication.view.seller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
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
import com.example.myapplication.view.adapter.AdapterCategorty
import com.example.myapplication.view.adapter.AdapterProductSeller
import com.example.myapplication.view.buyer.NotifikasiBuyerActivity
import com.example.myapplication.viewmodel.ViewModelProductSeller
import com.example.myapplication.viewmodel.ViewModelUser
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_daftar_jual_category.*
import kotlinx.android.synthetic.main.activity_daftar_jual_category.TV_nama
import kotlinx.android.synthetic.main.activity_daftar_jual_category.cardView_productSeller
import kotlinx.android.synthetic.main.activity_daftar_jual_category.daftarHistory
import kotlinx.android.synthetic.main.activity_daftar_jual_category.daftar_jualEdit
import kotlinx.android.synthetic.main.activity_daftar_jual_category.diminati_profileKota
import kotlinx.android.synthetic.main.activity_daftar_jual_category.kalaukosongHistory
import kotlinx.android.synthetic.main.activity_daftar_jual_history.*

@AndroidEntryPoint
class DaftarJualCategory : AppCompatActivity() {
    private lateinit var adapter : AdapterCategorty
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
        setContentView(R.layout.activity_daftar_jual_category)
        userManager = UserManager(this)
        val botnav = findViewById<BottomNavigationView>(R.id.navigation)
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
        addCtgy()
    }

    fun addCtgy(){
        btn_tambah_ctgy.setOnClickListener{
            val viewModelSellerCategory = ViewModelProvider(this)[ViewModelProductSeller::class.java]
            val dialogView = LayoutInflater.from(this).inflate(R.layout.customdialog_editctgy, null)
            val dialogbuilder = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            // Get references to views in the custom dialog
            val buttonUpdate = dialogView.findViewById<Button>(R.id.btn_editCtgy)
            buttonUpdate.setText("Tambahkan Category")
            buttonUpdate.setOnClickListener {
                val editTextname = dialogView.findViewById<EditText>(R.id.cd_edt_ctgy)
                viewModelSellerCategory.tambahCtgy(editTextname.text.toString())
                initView()
                dialogbuilder.dismiss()
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
            TV_nama.setText(it.nama)
            diminati_profileKota.setText(it.alamat)
        }
        getCategory()
    }

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