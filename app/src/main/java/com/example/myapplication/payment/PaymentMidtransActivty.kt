package com.example.myapplication.payment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.viewmodel.ViewModelProductSeller
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.ItemDetails
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import kotlinx.android.synthetic.main.activity_add_product_buyer.*
import kotlinx.android.synthetic.main.activity_payment_midtrans_activty.*

class PaymentMidtransActivty : AppCompatActivity() {
    var hargabarang:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_midtrans_activty)
        val idbarang = intent.getStringExtra("idproduk")
        val viewModel = ViewModelProvider(this)[ViewModelProductSeller::class.java]
        viewModel.getProductid(idbarang!!.toInt())
        viewModel.getproduk.observe(this){
            hargabarang = it.harga.toInt()
            Glide.with(this)
                .load(it.gambar)
                .override(400, 350)
                .into(produk_image)
            nama_produk.setText(it.nama)
            harga_produk.setText(it.harga)
        }
        SdkUIFlowBuilder.init()
            .setClientKey("SB-Mid-client-UyV8fwVUJHmHywYZ")
            .setContext(applicationContext)
            .setTransactionFinishedCallback({
                    result ->

            })
            .setMerchantBaseUrl("http://192.168.1.150/skripsi/midtrans")
            .enableLog(true)
            .setColorTheme(CustomColorTheme("#FFE51255", "#B61548", "#FFE51255"))
            .setLanguage("id")
            .buildSDK()

        pesan.setOnClickListener {
            val hargaproduct = hargabarang
            val Jumlah = etJumlah.text.toString()
            val catatan = etCatatan.text.toString()
            val convert = hargaproduct*Jumlah.toDouble()
            val transactionRequest = TransactionRequest("material-"+System.currentTimeMillis().toShort()+"",convert)
            val detail = ItemDetails("NamaItem",hargaproduct.toDouble(),Jumlah.toInt(),"testi")
            val itemDetails = ArrayList<ItemDetails>()
            itemDetails.add(detail)
        }
    }
}