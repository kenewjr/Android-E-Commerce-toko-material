package com.example.myapplication.payment

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.datastore.UserManager
import com.example.myapplication.viewmodel.ViewModelHome
import com.example.myapplication.viewmodel.ViewModelMidtrans
import com.example.myapplication.viewmodel.ViewModelProductSeller
import com.example.myapplication.viewmodel.ViewModelUser
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.BillingAddress
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ItemDetails
import com.midtrans.sdk.corekit.models.ShippingAddress
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_payment_midtrans_activty.*
import kotlinx.coroutines.DelicateCoroutinesApi


@DelicateCoroutinesApi
@AndroidEntryPoint
class PaymentMidtransActivty : AppCompatActivity(), TransactionFinishedCallback {
    var hargabarang:Int = 0
    var orderId : String = ""
    var produkid : Int = 0
    var gambar : String = ""
    private var idriwayat =0
    private lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_midtrans_activty)
        userManager = UserManager(this)
        viewModel()
        val viewModelProductSeller = ViewModelProvider(this)[ViewModelProductSeller::class.java]
        viewModelProductSeller.getHistory()
        viewModelProductSeller.datahistory.observe(this){
             val lastHistoryItem = it[it.size-1]
            idriwayat = lastHistoryItem.id.toInt()+1
            Log.e("idriwayat",idriwayat.toString())
        }
        getdataProfile()
        runOnUiThread {
            SdkUIFlowBuilder.init()
                .setClientKey("SB-Mid-client-UyV8fwVUJHmHywYZ")
                .setContext(this)
                .setTransactionFinishedCallback(this)
                .setMerchantBaseUrl("https://dev.vzcyberd.cloud/abrar/API/midtrans.php/")
                .enableLog(true)
                .setColorTheme(CustomColorTheme("#FFE51255", "#B61548", "#FFE51255"))
                .setLanguage("id")
                .buildSDK()
        }
        pesan()
        TransactionFinishedCallback {
            addHistory()
        }
    }

    fun pesan(){
        pesan.setOnClickListener {
            val Jumlah = etJumlah.text.toString()
            val convert = hargabarang*Jumlah.toDouble()
            val transactionRequest = TransactionRequest("material-"+System.currentTimeMillis().toShort()+"",convert)
            val detail = ItemDetails("NamaItem",hargabarang.toDouble(),Jumlah.toInt(),"testi")
            val itemDetails = ArrayList<ItemDetails>()
            itemDetails.add(detail)
            uiKitsDetails(transactionRequest)
            transactionRequest.customField1 = idriwayat.toString()
            transactionRequest.customField2 = Jumlah
            transactionRequest.customField3 = produkid.toString()
            transactionRequest.itemDetails = itemDetails
            MidtransSDK.getInstance().transactionRequest = transactionRequest
            MidtransSDK.getInstance().startPaymentUiFlow(this)
            orderId = transactionRequest.orderId
        }
    }

    fun getdataProfile(){
        val viewModelDataSeller = ViewModelProvider(this)[ViewModelUser::class.java]
        viewModelDataSeller.getProfile(id = userManager.fetchId()!!.toInt())
        viewModelDataSeller.profileData.observe(this) {
         etnamaBuyer.setText(it.nama)
            etphonenumber.setText(it.nohp)
            etadress.setText(it.alamat)
        }
    }
    fun uiKitsDetails(transactionRequest: TransactionRequest){
        val customerIdenty = etnamaBuyer.text.toString()
        val phone = etphonenumber.text.toString()
        val email = etemail.text.toString()
        val address = etadress.text.toString()
        val kodepos = etpostal.text.toString()
        val kota =  etcity.text.toString()
        val customersDetails = CustomerDetails()
        customersDetails.customerIdentifier = customerIdenty
        customersDetails.phone = phone
        customersDetails.email = email
        val shippingAddress = ShippingAddress()
        shippingAddress.address = address
        shippingAddress.city = kota
        shippingAddress.postalCode = kodepos
        customersDetails.shippingAddress = shippingAddress
        val billingAddress = BillingAddress()
        billingAddress.address = address
        billingAddress.city = kota
        billingAddress.postalCode = kodepos
        customersDetails.billingAddress = billingAddress
        transactionRequest.customerDetails = customersDetails
    }

    fun addHistory(){
        val viewModelProductSeller = ViewModelProvider(this)[ViewModelProductSeller::class.java]
        val viewModelMidtrans = ViewModelProvider(this)[ViewModelMidtrans::class.java]
        val viewModel = ViewModelProvider(this)[ViewModelHome::class.java]
        val idUser = userManager.fetchId()!!.toInt()
        val idbarang = intent.getStringExtra("idproduk")
        viewModelMidtrans.getStatus(orderId)
        viewModel.getProductid(idbarang!!.toInt())
        viewModel.productid.observe(this) {it ->
            viewModelMidtrans.datastatus.observe(this) {t->
                viewModelProductSeller.tambahHistory(
                    idUser,
                    idbarang.toInt(),
                    t.order_id,
                    t.metadata.extra_info.user_id,
                    t.transaction_time,
                    it.nama_produk,
                    hargabarang.toString(),
                    t.gross_amount,
                    etJumlah.text.toString(),
                    it.gambar
                )
            }
        }
    }
    private fun viewModel(){
        val viewModel = ViewModelProvider(this)[ViewModelHome::class.java]
        val idbarang = intent.getStringExtra("idproduk")
        produkid = idbarang!!.toInt()
        viewModel.getProductid(idbarang.toInt())
        viewModel.productid.observe(this@PaymentMidtransActivty) { it ->
            if (it != null) {
                hargabarang = it.harga.toInt()
                Glide.with(this)
                    .load(it.gambar)
                    .override(400, 350)
                    .into(produk_image)
                nama_produk.text = "Nama Produk : "+it.nama_produk
                harga_produk.text = "Harga Produk : "+it.harga
            }else {
                Log.e("midtranssss","kosong")
            }
        }
    }

    override fun onTransactionFinished(transactionResult: TransactionResult) {

        when {
            transactionResult.response != null -> {
                when (transactionResult.status) {
                    TransactionResult.STATUS_SUCCESS -> {
                        Toast.makeText(this, "Success transaction", Toast.LENGTH_LONG).show()
                    }
                    TransactionResult.STATUS_PENDING -> {
                        addHistory()
                        Toast.makeText(this, "Pending transaction", Toast.LENGTH_LONG).show()
                    }
                    TransactionResult.STATUS_FAILED -> {
                        Toast.makeText(this, "Failed ${transactionResult.response.statusMessage}", Toast.LENGTH_LONG).show()
                    }
                }
            }
            transactionResult.isTransactionCanceled -> {
                Toast.makeText(this, "Canceled transaction", Toast.LENGTH_LONG).show()
            }
            else -> {
                if (transactionResult.status.equals(TransactionResult.STATUS_INVALID, true))
                    Toast.makeText(this, "Invalid transaction", Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(this, "Failure transaction", Toast.LENGTH_LONG).show()
            }
        }
    }
}