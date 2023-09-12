@file:Suppress("UselessCallOnNotNull")

package com.example.myapplication.view.buyer


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.datastore.UserManager
import com.example.myapplication.model.GetAllProdukItem
import com.example.myapplication.network.ApiClient
import com.example.myapplication.payment.PaymentMidtransActivty
import com.example.myapplication.viewmodel.ViewModelHome
import com.example.myapplication.viewmodel.ViewModelProductSeller
import com.example.myapplication.viewmodel.ViewModelUser
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add_product_buyer.*
import kotlinx.android.synthetic.main.activity_add_product_buyer.back
import kotlinx.android.synthetic.main.custom_dialog_hargatawar_buyer.view.*
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
@AndroidEntryPoint
class AddProductBuyerActivity : AppCompatActivity() {
    private lateinit var userManager: UserManager
    private lateinit var apiClient: ApiClient
    private var datalengkap: String = "ada"
    private var produk: String = ""
    private var produkpilih: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product_buyer)
        userManager = UserManager(this)
        apiClient = ApiClient()
        back.setOnClickListener {
            onBackPressed()
        }
        addProductBuyer_btnTertarik.setOnClickListener {
            val dataProduct = intent.extras!!.getSerializable("detailproduk") as GetAllProdukItem?
            val pindah = Intent(this, PaymentMidtransActivty::class.java)
            pindah.putExtra("idproduk",dataProduct!!.id)
            startActivity(pindah)
        }
//        disablebutton()
        detailData()
    }

//    private fun disablebutton(){
//        userManager = UserManager(this)
//        val viewModel = ViewModelProvider(this)[ViewModelUser::class.java]
//        viewModel.getProfile(userManager.fetchId()!!.toInt())
//        runOnUiThread {
//        viewModel.order.observe(this) {
//            for (z in it.indices) {
//                produk = it[z].productId.toString()
//                if (produkpilih == produk) {
//                    addProductBuyer_btnTertarik.text =
//                        "Menunggu Respon Penjual Atau Batalkan Orderan"
//                    addProductBuyer_btnTertarik.setOnClickListener {
//                        startActivity(Intent(this@AddProductBuyerActivity,OrderBuyer::class.java))
//                    }
//                }
//
//            }
//        }
//        }
//    }

    private fun detailData() {
        userManager = UserManager(this)
        val dataProduct = intent.extras!!.getSerializable("detailproduk") as GetAllProdukItem?

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
        }
        if (dataProduct != null) {
            val viewModel = ViewModelProvider(this)[ViewModelProductSeller::class.java]
            viewModel.getProductid(dataProduct.id.toInt())
            Glide.with(this)
                .load(dataProduct.gambar)
                .override(400, 350)
                .into(tv_imgdetailproduct)
            produkpilih = dataProduct.id.toString()
            tv_judulproductdetail.text = dataProduct.nama
            tv_acesorisproductdetail.text = dataProduct.kategori.toString()
            tv_hargaproductdetail.text = dataProduct.harga.toString()
            tv_deskripsidetail.text = dataProduct.deskripsi
            tv_acesorisproductdetail.text = ""
        }
    }
}