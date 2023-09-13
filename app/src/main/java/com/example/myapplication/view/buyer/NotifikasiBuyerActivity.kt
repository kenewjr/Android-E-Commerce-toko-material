@file:Suppress("CanBeVal", "DEPRECATION")

package com.example.myapplication.view.buyer


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.datastore.UserManager
import com.example.myapplication.network.ApiClient
import com.example.myapplication.view.HomeActivity
import com.example.myapplication.view.LoginActivity
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
//    private lateinit var adapterNotifikasiBuyer: AdapterNotifikasiBuyer
    private val bottomNavigasi = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId){
            R.id.notifikasi -> {
                startActivity(Intent(this, NotifikasiBuyerActivity::class.java))
                return@OnNavigationItemSelectedListener true
            }
            R.id.dashboard -> {
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
                Toast.makeText(this, "Kamu Sedang Berada Di Akun", Toast.LENGTH_SHORT).show()
                return@OnNavigationItemSelectedListener false
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
        setContentView(R.layout.activity_notifikasi_buyer)
        apiClient = ApiClient()
        userManager = UserManager(this)
        val botnav = findViewById<BottomNavigationView>(R.id.navigation)
        botnav.setOnNavigationItemSelectedListener(bottomNavigasi)
//        fetchnotif()

    }

//    private fun fetchnotif(){
//        apiClient.getApiService().getNotif(token = userManager.fetchAuthToken().toString()).enqueue(object : Callback<List<GetNotifikasiItem>>{
//            override fun onResponse(
//                call: Call<List<GetNotifikasiItem>>,
//                response: Response<List<GetNotifikasiItem>>
//            ) {
//                if(response.isSuccessful){
//                    adapterNotifikasiBuyer = AdapterNotifikasiBuyer(response.body()!!) {
//                        apiClient.getApiService()
//                            .patchNotif(token = userManager.fetchAuthToken().toString(),it.id)
//                            .enqueue(object : Callback<GetNotifikasiItem>{
//                                override fun onResponse(
//                                    call: Call<GetNotifikasiItem>,
//                                    response: Response<GetNotifikasiItem>
//                                ) {
//                                    if (response.isSuccessful ){
//                                        var livedata : MutableLiveData<GetNotifikasiItem> = MutableLiveData()
//                                        livedata.postValue(response.body())
//                                        recreate()
//                                    }
//                                }
//
//                                override fun onFailure(
//                                    call: Call<GetNotifikasiItem>,
//                                    t: Throwable
//                                ) {
//                                 //
//                                }
//
//                            })
//                    }
//
//                    rv_notifikasiBuyer.layoutManager = LinearLayoutManager(applicationContext)
//                    rv_notifikasiBuyer.adapter = adapterNotifikasiBuyer
//
//                }
//            }
//
//            override fun onFailure(call: Call<List<GetNotifikasiItem>>, t: Throwable) {
//                //
//            }
//
//        })
//    }

    override fun onResume() {
        super.onResume()
//        fetchnotif()
    }
}