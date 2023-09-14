package com.example.myapplication.payment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.viewmodel.ViewModelHome
import com.example.myapplication.viewmodel.ViewModelProductSeller
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.BillingAddress
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ItemDetails
import com.midtrans.sdk.corekit.models.ShippingAddress
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_payment_midtrans_activty.*
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
@AndroidEntryPoint
class PaymentMidtransActivty : AppCompatActivity() {
    var hargabarang:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_midtrans_activty)
        viewModel()

        SdkUIFlowBuilder.init()
            .setClientKey("SB-Mid-client-UyV8fwVUJHmHywYZ")
            .setContext(applicationContext)
            .setTransactionFinishedCallback({
                    result ->
            })
            .setMerchantBaseUrl("http://192.168.1.150/skripsi/midtrans/")
            .enableLog(true)
            .setColorTheme(CustomColorTheme("#FFE51255", "#B61548", "#FFE51255"))
            .setLanguage("id")
            .buildSDK()

        pesan.setOnClickListener {
            val Jumlah = etJumlah.text.toString()
            val catatan = etCatatan.text.toString()
            val convert = hargabarang*Jumlah.toDouble()
            Log.e("convert",convert.toString())
            val transactionRequest = TransactionRequest("material-"+System.currentTimeMillis().toShort()+"",convert)
            val detail = ItemDetails("NamaItem",hargabarang.toDouble(),Jumlah.toInt(),"testi")
            val itemDetails = ArrayList<ItemDetails>()
            itemDetails.add(detail)
            uiKitsDetails(transactionRequest)
            transactionRequest.itemDetails =itemDetails
            MidtransSDK.getInstance().transactionRequest = transactionRequest
            MidtransSDK.getInstance().startPaymentUiFlow(this)
        }

    }
    fun SdkUI(){

    }
    fun pesan(){

    }
    fun uiKitsDetails(transactionRequest: TransactionRequest){
        val customersDetails = CustomerDetails()
        customersDetails.customerIdentifier = "abrar"
        customersDetails.phone = "08897776"
        val shippingAddress = ShippingAddress()
        shippingAddress.address = "cilegon,banten"
        shippingAddress.city = "tanggerang"
        shippingAddress.postalCode = "42415"
        customersDetails.shippingAddress = shippingAddress
        val billingAddress = BillingAddress()
        billingAddress.address = "cilegon,banten"
        billingAddress.city = "tanggerang"
        billingAddress.postalCode = "42415"
        customersDetails.billingAddress = billingAddress

        transactionRequest.customerDetails = customersDetails
    }

    private fun viewModel(){
        val viewModel = ViewModelProvider(this)[ViewModelHome::class.java]
        val idbarang = intent.getStringExtra("idproduk")
        viewModel.getProductid(22)
        viewModel.productid.observe(this@PaymentMidtransActivty) { it ->
            if (it != null) {
                Log.e(TAG, it.nama_produk.toString())
                hargabarang = it.harga.toInt()
                Glide.with(this)
                    .load(it.gambar)
                    .override(400, 350)
                    .into(produk_image)
                nama_produk.text = it.nama_produk
                harga_produk.text = it.harga
                Log.e("harga",hargabarang.toString())
            }else {
                Log.e("midtranssss","kosong")
            }
        }
    }
}