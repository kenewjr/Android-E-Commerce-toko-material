@file:Suppress("UselessCallOnNotNull")

package com.example.myapplication.view.buyer


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
import com.example.myapplication.viewmodel.ViewModelHome
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add_product_buyer.*
import kotlinx.android.synthetic.main.activity_add_product_buyer.back
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
@AndroidEntryPoint
class AddProductBuyerActivity : AppCompatActivity() {
    private lateinit var userManager: UserManager
//    private lateinit var apiClient: ApiClient
    private var datalengkap :String = "ada"
    private var produk : String = ""
    private var produkpilih : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product_buyer)
        userManager = UserManager(this)
//        apiClient = ApiClient()
        back.setOnClickListener {
            onBackPressed()
        }
//        disablebutton()
//        detailData()
//        postFavorite()
    }

//    private fun disablebutton(){
//        userManager = UserManager(this)
//        val viewModel = ViewModelProvider(this)[ViewModelHome::class.java]
//        viewModel.fetchbuyerorder(userManager.fetchAuthToken().toString())
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
//
//    private fun detailData(){
//        userManager = UserManager(this)
//        val dataProduct = intent.extras!!.getSerializable("detailproduk") as GetBuyerProductItem?
//        val dataProductnotif = intent.extras!!.getSerializable("detailnotif") as GetNotifikasiItem?
//
//        val viewModelDataSeller = ViewModelProvider(this)[ViewModelUser::class.java]
//        viewModelDataSeller.getProfiler(token = userManager.fetchAuthToken().toString())
//        viewModelDataSeller.profileData.observe(this) {
//            if (it.fullName.isNullOrEmpty() ||
//                it.email.isNullOrEmpty() ||
//                it.imageUrl.isNullOrEmpty() ||
//                it.address.isNullOrEmpty() ||
//                it.city.isNullOrEmpty() ||
//                it.password.isNullOrEmpty() ||
//                it.phoneNumber.toString().isNullOrEmpty()
//            ) {
//                datalengkap = "kosong"
//            }
//        }
//        if (dataProduct != null) {
//            val viewModel = ViewModelProvider(this)[ViewModelHome::class.java]
//            viewModel.detailproduct(dataProduct.id)
//            viewModel.detail.observe(this){
//                Glide.with(this@AddProductBuyerActivity).load(it.user.imageUrl).override(45,45)
//                    .into(IV_penjual)
//                addBuyer_kota.text = it.user.city
//                TV_nama.text = it.user.fullName
//            }
//            Glide.with(this)
//                .load(dataProduct.imageUrl)
//                .override(400,350)
//                .into(tv_imgdetailproduct)
//            produkpilih = dataProduct.id.toString()
//            tv_judulproductdetail.text = dataProduct.name
//            tv_acesorisproductdetail.text = dataProduct.categories.toString()
//            tv_hargaproductdetail.text = dataProduct.basePrice.toString()
//            tv_deskripsidetail.text = dataProduct.description
//            tv_acesorisproductdetail.text = ""
//            if (dataProduct.categories.isNotEmpty()){
//                for (i in dataProduct.categories.indices){
//                    if (dataProduct.categories.lastIndex == 0){
//                        tv_acesorisproductdetail.text = dataProduct.categories[i].name
//                        break
//                    }
//                    if (i == 0) {
//                        tv_acesorisproductdetail.text = dataProduct.categories[i].name + ","
//                    } else if (i != dataProduct.categories.lastIndex && i > 0){
//                        tv_acesorisproductdetail.text = tv_acesorisproductdetail.text.toString() +
//                        dataProduct.categories[i].name  + ","
//                    } else {
//                        tv_acesorisproductdetail.text = tv_acesorisproductdetail.text.toString() +
//                        dataProduct.categories[i].name
//                    }
//                }
//            } else {
//                tv_acesorisproductdetail.text = "Lainnya"
//            }
//            if (dataProductnotif != null){
//                addProductBuyer_btnTertarik.text = "Menunggu Respon Penjual"
//            }else {
//                addProductBuyer_btnTertarik.setOnClickListener {
//                    Log.e("dada", datalengkap)
//                    userManager.ceklogin.asLiveData().observe(this){
//                        if (it == true && datalengkap == "ada"){
//                            iniDialogTawarHarga()
//                        } else if(datalengkap == "kosong") {
//                            Toast.makeText(this, "Data Anda Belum Lengkap", Toast.LENGTH_SHORT)
//                                .show()
//                            startActivity(Intent(this, AkunSayaActivity::class.java))
//                            finish()
//                        }else{
//                            Toast.makeText(this, "Anda Belum Login", Toast.LENGTH_SHORT).show()
//                            startActivity(Intent(this, LoginActivity::class.java))
//                            finish()
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//
//    private fun iniDialogTawarHarga(){
//        userManager = UserManager(this)
//        val dialog = BottomSheetDialog(this)
//        val dialogView = layoutInflater.inflate(R.layout.custom_dialog_hargatawar_buyer, null)
//        val detailBarang = intent.extras!!.getSerializable("detailproduk") as GetBuyerProductItem
//        val  btnTawaran = dialogView.ca_hargatawar_btnok
//        dialogView.customDialog_namaProduk.text = detailBarang.name
//        dialogView.custom_hargaproduk.text = "Harga : Rp. ${detailBarang.basePrice}"
//        Glide.with(dialogView.customDialog_gambarProduk.context)
//            .load(detailBarang.imageUrl)
//            .error(R.drawable.ic_launcher_background)
//            .into(dialogView.customDialog_gambarProduk)
//        dialogView.custum_Categoriproduct.text = ""
//        if (detailBarang.categories.isNotEmpty()){
//            for (i in detailBarang.categories.indices){
//                if (detailBarang.categories.lastIndex == 0) {
//                    dialogView.custum_Categoriproduct.text =
//                        "Kategori: ${detailBarang.categories[i].name}"
//                    break
//                }
//                if (i == 0) {
//                    dialogView.custum_Categoriproduct.text =
//                        "Kategori: ${detailBarang.categories[i].name}, "
//                } else if (i != detailBarang.categories.lastIndex && i > 0) {
//                    dialogView.custum_Categoriproduct.text =
//                        dialogView.custum_Categoriproduct.text.toString() + detailBarang.categories[i].name + ","
//                } else {
//                    dialogView.custum_Categoriproduct.text =
//                        dialogView.custum_Categoriproduct.text.toString() +
//                                detailBarang.categories[i].name
//                }
//            }
//        } else {
//            dialogView.custum_Categoriproduct.text = "Kategori Tidak ditemukan"
//        }
//
//        btnTawaran.setOnClickListener {
//            val productId = detailBarang.id
//            val edtTawar = dialogView.ca_hargatawar.text.toString().toInt()
//                if (edtTawar.toString().isNotEmpty()) {
//                    val buyerOrderViewModel =
//                        ViewModelProvider(this)[BuyerOrderViewModel::class.java]
//                    buyerOrderViewModel.postBuyerOrder(
//                        userManager.fetchAuthToken().toString(),
//                        PostBuyerOrder(productId, edtTawar)
//                    )
//                    Toast.makeText(this, "Tawaran sudah dikirim", Toast.LENGTH_SHORT).show()
//                    dialog.dismiss()
//                }
//            recreate()
//            }
//
//        dialog.setCancelable(true)
//        dialog.setContentView(dialogView)
//        dialog.show()
//    }
//
//    private fun postFavorite(){
//        userManager = UserManager(this)
//        val buyerWishList = ViewModelProvider(this)[ViewModelWishList::class.java]
//
//        imageWishList.setOnClickListener {
//            userManager.ceklogin.asLiveData().observe(this){
//                if (it == true){
//                    val detailBarangd = intent.extras!!.getSerializable("detailproduk") as GetBuyerProductItem
//                    val productID = detailBarangd.id
//                    buyerWishList.postWishListBuyer(userManager.fetchAuthToken().toString(), productID)
//                    imageWishList.setImageResource(R.drawable.favorite_click)
//                    Toast.makeText(this, "Berhasil Menambahkan Wish List Anda", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(this, "Anda Belum Login", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//        }
//    }
}