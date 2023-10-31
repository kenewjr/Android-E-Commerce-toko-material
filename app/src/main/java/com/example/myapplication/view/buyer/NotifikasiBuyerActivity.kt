@file:Suppress("CanBeVal", "DEPRECATION")

package com.example.myapplication.view.buyer


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.datastore.UserManager
import com.example.myapplication.model.GetHistoryItem
import com.example.myapplication.network.ApiClient
import com.example.myapplication.view.AkunsayaActivty
import com.example.myapplication.view.HomeActivity
import com.example.myapplication.view.LoginActivity
import com.example.myapplication.view.adapter.AdapterNotifikasiBuyer
import com.example.myapplication.view.seller.DaftarJualActivity
import com.example.myapplication.view.seller.LengkapiDetailProductActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_notifikasi_buyer.*
import kotlinx.coroutines.DelicateCoroutinesApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@DelicateCoroutinesApi
@AndroidEntryPoint
class NotifikasiBuyerActivity : AppCompatActivity() {
    private lateinit var  userManager: UserManager
    private lateinit var apiClient: ApiClient
    private lateinit var adapterNotifikasiBuyer: AdapterNotifikasiBuyer

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifikasi_buyer)
        apiClient = ApiClient()
        userManager = UserManager(this)
        val booleanvalue = userManager.getBooleanValue()
        if (booleanvalue && userManager.fetchstatus() == "seller") {
            val botnav = findViewById<BottomNavigationView>(R.id.navigation)
            val botnav2 = findViewById<BottomNavigationView>(R.id.default_navigation)
            botnav2.isInvisible = true
            botnav.setOnNavigationItemSelectedListener(bottomNavigasi)
            fetchnotifseller()
        } else {
            fetchnotif()
            val botnav = findViewById<BottomNavigationView>(R.id.default_navigation)
            val botnav2 = findViewById<BottomNavigationView>(R.id.navigation)
            botnav2.isInvisible = true
            notifikasiBuyer_welcome.text = "History"
            botnav.setOnNavigationItemSelectedListener(bottomNavigasi)
        }
    }

    private fun fetchnotif(){
        apiClient.getApiService().getHistoryUserID(userManager.fetchId()!!.toInt())
            .enqueue(object : Callback<List<GetHistoryItem>>{
            override fun onResponse(
                call: Call<List<GetHistoryItem>>,
                response: Response<List<GetHistoryItem>>
            ) {
                if (response.isSuccessful) {
                    adapterNotifikasiBuyer = AdapterNotifikasiBuyer(response.body()!!) {
                        val clickedproduct = Bundle()
                        clickedproduct.putSerializable("detailorder",it)
                        val pindah = Intent(this@NotifikasiBuyerActivity, HistoryBuyerActivity::class.java)
                        pindah.putExtras(clickedproduct)
                        startActivity(pindah)
                    }
                    rv_notifikasiBuyer.layoutManager = LinearLayoutManager(applicationContext)
                    rv_notifikasiBuyer.adapter = adapterNotifikasiBuyer
                }else{
                    Toast.makeText(this@NotifikasiBuyerActivity, "Anda Belom Berbelanja", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<GetHistoryItem>>, t: Throwable) {
                Log.e("error",t.message.toString())
            }
    })
    }

    private fun fetchnotifseller(){
        apiClient.getApiService().getHistory()
            .enqueue(object : Callback<List<GetHistoryItem>>{
                override fun onResponse(
                    call: Call<List<GetHistoryItem>>,
                    response: Response<List<GetHistoryItem>>
                ) {
                    if (response.isSuccessful) {
                        adapterNotifikasiBuyer = AdapterNotifikasiBuyer(response.body()!!) {
                            val clickedproduct = Bundle()
                            clickedproduct.putSerializable("detailorder",it)
                            val pindah = Intent(this@NotifikasiBuyerActivity, HistoryBuyerActivity::class.java)
                            pindah.putExtras(clickedproduct)
                            startActivity(pindah)
                        }
                        rv_notifikasiBuyer.layoutManager = LinearLayoutManager(applicationContext)
                        rv_notifikasiBuyer.adapter = adapterNotifikasiBuyer
                    }else{
                        Toast.makeText(this@NotifikasiBuyerActivity, "Belom Ada Pembeli Yang Beli", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<List<GetHistoryItem>>, t: Throwable) {
                    Log.e("error",t.message.toString())
                }
            })
    }
    override fun onResume() {
        super.onResume()
        fetchnotif()
    }
}