package com.example.myapplication.payment

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.datastore.UserManager
import com.example.myapplication.viewmodel.ViewModelHome
import com.example.myapplication.viewmodel.ViewModelMidtrans
import com.example.myapplication.viewmodel.ViewModelProductSeller
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
class PaymentMidtransActivty : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    var hargabarang:Int = 0
    var orderId : String = ""
    private lateinit var userManager: UserManager
    private val list = ArrayList<TransactionRequest>()
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_midtrans_activty)
        initializeSwipeRefreshLayout()
        viewModel()
        SdkUI()
        pesan()
    }
    fun SdkUI(){
        SdkUIFlowBuilder.init()
            .setClientKey("SB-Mid-client-UyV8fwVUJHmHywYZ")
            .setContext(applicationContext)
            .setTransactionFinishedCallback { result ->
                if (result.response != null) {
                    when (result.status) {
                        TransactionResult.STATUS_SUCCESS -> Toast.makeText(
                            this,
                            "Transaction Finished. ID: " + result.response.transactionId,
                            Toast.LENGTH_LONG
                        ).show()
                        TransactionResult.STATUS_PENDING -> Toast.makeText(
                            this,
                            "Transaction Pending. ID: " + result.response.transactionId,
                            Toast.LENGTH_LONG
                        ).show()
                        TransactionResult.STATUS_FAILED -> Toast.makeText(
                            this,
                            "Transaction Failed. ID: " + result.response.transactionId + ". Message: " + result.response.statusMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    result.response.statusMessage
                    addHistory()
                } else if (result.isTransactionCanceled) {
                    Toast.makeText(this, "Transaction Canceled", Toast.LENGTH_LONG).show()
                } else {
                    if (result.status.equals(TransactionResult.STATUS_INVALID, ignoreCase = true)) {
                        Toast.makeText(this, "Transaction Invalid", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Transaction Finished with failure.", Toast.LENGTH_LONG).show()
                    }
                }
            }
            .setMerchantBaseUrl("http://192.168.1.150/skripsi/midtrans.php/")
            .enableLog(true)
            .setColorTheme(CustomColorTheme("#FFE51255", "#B61548", "#FFE51255"))
            .setLanguage("id")
            .buildSDK()
    }

    fun pesan(){
        pesan.setOnClickListener {
            val Jumlah = etJumlah.text.toString()
            val catatan = etCatatan.text.toString()
            val convert = hargabarang*Jumlah.toDouble()
            val transactionRequest = TransactionRequest("material-"+System.currentTimeMillis().toShort()+"",convert)
            val detail = ItemDetails("NamaItem",hargabarang.toDouble(),Jumlah.toInt(),"testi")
            val itemDetails = ArrayList<ItemDetails>()
            itemDetails.add(detail)
            uiKitsDetails(transactionRequest)
            transactionRequest.itemDetails =itemDetails
            MidtransSDK.getInstance().transactionRequest = transactionRequest
            MidtransSDK.getInstance().startPaymentUiFlow(this)
            orderId = transactionRequest.orderId
        }
    }
    fun uiKitsDetails(transactionRequest: TransactionRequest){
        val customersDetails = CustomerDetails()
        customersDetails.customerIdentifier = "abrar"
        customersDetails.phone = "08897776"
        customersDetails.email = "sukijah@gmail.com"
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

    fun addHistory(){
        val viewModelProductSeller = ViewModelProvider(this)[ViewModelProductSeller::class.java]
        val viewModelMidtrans = ViewModelProvider(this)[ViewModelMidtrans::class.java]
        val viewModel = ViewModelProvider(this)[ViewModelHome::class.java]
        val idUser = userManager.fetchId()!!.toInt()
        val idbarang = intent.getStringExtra("idproduk")
        var gambar : String = ""
        var namaProduk : String = ""
        viewModel.getProductid(idbarang!!.toInt())
        viewModel.productid.observe(this){
            gambar = it.gambar
            namaProduk = it.nama_produk
        }
        viewModelMidtrans.getStatus(orderId)
        viewModelMidtrans.datastatus.observe(this){
            viewModelProductSeller.tambahHistory(
                idUser,
                idbarang!!.toInt(),
                it.metadata.extra_info.user_id,it.transaction_time,
                namaProduk,
                hargabarang.toString(),
                it.gross_amount,
                etJumlah.text.toString(),
                gambar
            )
        }



    }
    private fun viewModel(){
        val viewModel = ViewModelProvider(this)[ViewModelHome::class.java]
        val idbarang = intent.getStringExtra("idproduk")
        viewModel.getProductid(idbarang!!.toInt())
        viewModel.productid.observe(this@PaymentMidtransActivty) { it ->
            if (it != null) {
                hargabarang = it.harga.toInt()
                Glide.with(this)
                    .load(it.gambar)
                    .override(400, 350)
                    .into(produk_image)
                nama_produk.text = it.nama_produk
                harga_produk.text = it.harga
            }else {
                Log.e("midtranssss","kosong")
            }
        }
    }

    private fun initializeSwipeRefreshLayout() {
        swipeRefreshLayout = findViewById(R.id.swipeup_refresh)
        swipeRefreshLayout.setOnRefreshListener(this)
        swipeRefreshLayout.post {
            swipeRefreshLayout.isRefreshing = true
            list.clear()
            loadData()
        }
    }

    override fun onRefresh() {
        list.removeAt(0)
        loadData()
    }

    private fun loadData() {
        swipeRefreshLayout.isRefreshing = true
        SdkUI()
        swipeRefreshLayout.isRefreshing = false
    }
}