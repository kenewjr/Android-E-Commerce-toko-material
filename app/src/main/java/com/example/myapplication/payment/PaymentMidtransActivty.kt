@file:Suppress("LocalVariableName", "MemberVisibilityCanBePrivate", "MemberVisibilityCanBePrivate")

package com.example.myapplication.payment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.datastore.UserManager
import com.example.myapplication.model.GetAllPengirimanItem
import com.example.myapplication.model.GetHistoryItem
import com.example.myapplication.model.GetPromoItem
import com.example.myapplication.network.ApiClient
import com.example.myapplication.view.HomeActivity
import com.example.myapplication.view.buyer.NotifikasiBuyerActivity
import com.example.myapplication.viewmodel.ViewModelHome
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@DelicateCoroutinesApi
@AndroidEntryPoint
class PaymentMidtransActivty : AppCompatActivity(), TransactionFinishedCallback {
    private var hargabarang:Int = 0
    private var orderId : String = ""
    private var produkid : Int = 0
    var gambar : String = ""
    var maxberat : String = "0"
    var beratbarang = 0.0
    var namabarang = ""
    var totalhitung = 0
    private var minimharga = 0
    private var maxharga = 0
    private var hargadiskon = 0
    private var idriwayat = 0
    private lateinit var userManager: UserManager
    private lateinit var selectedOngkos: GetAllPengirimanItem
    private lateinit var selectedPromo: GetPromoItem
    private lateinit var apiClient: ApiClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_midtrans_activty)
        userManager = UserManager(this)
        apiClient = ApiClient()
        viewModel()
        getPengiriman()
        val viewModelProductSeller = ViewModelProvider(this)[ViewModelProductSeller::class.java]
        viewModelProductSeller.getHistory()
        viewModelProductSeller.datahistory.observe(this){
            val lastHistoryItem = it.first()
            idriwayat = lastHistoryItem.id.toInt()+1
        }
        getPromo()
        getdataProfile()
        runOnUiThread {
            SdkUIFlowBuilder.init()
                .setClientKey("SB-Mid-client-UyV8fwVUJHmHywYZ")
                .setContext(this)
                .setTransactionFinishedCallback(this)
                .setMerchantBaseUrl("http://abrar.vzcyberd.my.id/API/midtrans.php/")
                .enableLog(true)
                .setColorTheme(CustomColorTheme("#FFE51255", "#B61548", "#FFE51255"))
                .setLanguage("id")
                .buildSDK()
        }
        pesan()
        btn_home.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        etJumlah.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                totalhitung = calculateResult(p0.toString())
                tv_jmlHarga.text = "Total Harga: Rp.$totalhitung"
            }
        })
    }

    private fun doubleCheck(
        nama: String,
        phone: String,
        email: String,
        address: String,
        postal: String,
        city: String,
        jumlah: String,
        ttlberat: Double,
        totalhitung: Int
    ): Boolean {
        if (isEmptyAndToast(nama, "Username harus diisi")) return false
        if (isEmptyAndToast(phone, "Nomor HP harus diisi")) return false
        if (!isValidEmailAndToast(email, "Email harus diisi dengan benar")) return false
        if (isEmptyAndToast(address, "Alamat harus diisi")) return false
        if (isEmptyAndToast(postal, "KodePos harus diisi")) return false
        if (isEmptyAndToast(city, "Kota harus diisi")) return false
        if (isEmptyAndToast(jumlah, "Jumlah Produk harus diisi")) return false
        if (select_ongkos.isEmpty()) {
            Toast.makeText(this, "Ongkos harus diisi", Toast.LENGTH_SHORT).show()
            return false
        }
        if (ttlberat >= maxberat.toLong()) {
            showToast("Barang Terlalu Berat Pilih Ongkir Yang Lain")
            return false
        }
        if (minimharga == 0 && maxharga == 0) {
            return true
        }
            if (maxharga <= totalhitung || minimharga >= totalhitung) {
            showToast("Harga Barang Tidak Memenuhi Jarak Untuk Menggunakan Diskon")
            select_diskon.setSelection(0)
            return false
        }else {
            return true
        }
        return true
    }

    private fun isEmptyAndToast(value: String, message: String): Boolean {
        if (value.isEmpty()) {
            showToast(message)
            return true
        }
        return false
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)(@)(.+)(\\.)(.+)"
        return email.matches(emailRegex.toRegex())
    }
    private fun isValidEmailAndToast(email: String, message: String): Boolean {
        if (!isValidEmail(email)) {
            showToast(message)
            return false
        }
        return true
    }

    private fun getPengiriman(){
        val viewModelSeller= ViewModelProvider(this)[ViewModelProductSeller::class.java]
        viewModelSeller.sellerPengiriman.observe(this){ it ->
            val categoryNames = it.map { it.kendaraan }.toMutableList()
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            select_ongkos.adapter = adapter
            select_ongkos.onItemSelectedListener  = object  : AdapterView.OnItemSelectedListener{
                @SuppressLint("SetTextI18n")
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    selectedOngkos = it[p2]
                    maxberat = it[p2].max_berat
                    tv_beratpengiriman.text = "Max Berat Ongkir : "+it[p2].max_berat
                    tv_hargapengiriman.text = "Ongkos : Rp."+it[p2].harga
                    totalhitung = calculateResult(etJumlah.text.toString())
                    tv_jmlHarga.text = "Total Harga: Rp.$totalhitung"
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    //
                }
            }
        }
        viewModelSeller.getSellerPengiriman()
    }

    private fun getPromo() {
        val viewModelProductSeller = ViewModelProvider(this)[ViewModelProductSeller::class.java]
        viewModelProductSeller.sellerPromo.observe(this) { it ->
            val diskonrange = it.map { "Rp.${it.min_harga} - Rp.${it.max_harga}" }.toMutableList()
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, diskonrange)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            select_diskon.adapter = adapter
            select_diskon.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    selectedPromo = it[p2]
                    tv_hargapromo.isVisible = true
                    tv_hargapromo.text = "Diskon : Rp."+selectedPromo.harga_diskon
                    hargadiskon = selectedPromo.harga_diskon.toInt()
                    minimharga = it[p2].min_harga.toInt()
                    maxharga = it[p2].max_harga.toInt()
                    totalhitung = calculateResult(etJumlah.text.toString())
                    tv_jmlHarga.text = "Total Harga: Rp.$totalhitung"
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    //
                }
            }
        }
        viewModelProductSeller.getPromo()
    }

    @SuppressLint("SetTextI18n")
    private fun calculateResult(input: String): Int {
        return try {
            val number = input.toInt()
            (number * hargabarang) + selectedOngkos.harga.toInt()
        } catch (e: NumberFormatException) {
            hargabarang // Return default value if parsing fails
        }
    }

    private fun pesan(){
        pesan.setOnClickListener {
            val nama = etnamaBuyer.text.toString()
            val phone = etphonenumber.text.toString()
            val email = etemail.text.toString()
            val adress = etadress.text.toString()
            val postal = etpostal.text.toString()
            val kota = etcity.text.toString()
            val jumlah = etJumlah.text.toString()
            totalhitung = ((hargabarang.toInt()) * jumlah.toInt()) + selectedOngkos.harga.toInt()
            val ttlberat: Double = try {
                beratbarang*jumlah.toInt()
            } catch (e: NumberFormatException) {
                beratbarang*jumlah.toInt()
            }
            if (doubleCheck(nama,phone,email,adress,postal,kota,jumlah,ttlberat,totalhitung)) {
                val Jumlah = etJumlah.text.toString()
                val totalAmount = (hargabarang.toDouble() * Jumlah.toInt()) + selectedOngkos.harga.toDouble() - hargadiskon.toDouble()
                val transactionRequest = TransactionRequest(
                    "material-" + System.currentTimeMillis().toShort() + "",
                    totalAmount
                )
                val detail = ItemDetails("Produk", hargabarang.toDouble(), Jumlah.toInt(), namabarang)
                val detail2 = ItemDetails("Jenis Pengiriman", selectedOngkos.harga.toDouble(), 1, selectedOngkos.kendaraan)
                val detail3 = ItemDetails("Diskon", -hargadiskon.toDouble(), 1, "Diskon")
                val itemDetails = ArrayList<ItemDetails>()
                itemDetails.add(detail)
                itemDetails.add(detail2)
                itemDetails.add(detail3)
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
    }

    private fun getdataProfile(){
        val viewModelDataSeller = ViewModelProvider(this)[ViewModelUser::class.java]
        viewModelDataSeller.getProfile(id = userManager.fetchId()!!.toInt())
        viewModelDataSeller.profileData.observe(this) {
         etnamaBuyer.setText(it.nama)
            etphonenumber.setText(it.nohp)
            etadress.setText(it.alamat)
            etemail.setText(it.email)
            etcity.setText(it.kota)
            etpostal.setText(it.kodepos)
        }
    }
    private fun uiKitsDetails(transactionRequest: TransactionRequest){
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
        customersDetails.firstName = customerIdenty
        val shippingAddress = ShippingAddress()
        shippingAddress.address = address
        shippingAddress.city = kota
        shippingAddress.postalCode = kodepos
        shippingAddress.phone = phone
        shippingAddress.firstName = customerIdenty
        customersDetails.shippingAddress = shippingAddress
        val billingAddress = BillingAddress()
        billingAddress.address = address
        billingAddress.city = kota
        billingAddress.postalCode = kodepos
        customersDetails.billingAddress = billingAddress
        transactionRequest.customerDetails = customersDetails
    }

    private fun addHistory() {
        val viewModelProductSeller = ViewModelProvider(this)[ViewModelProductSeller::class.java]
        val idUser = userManager.fetchId()!!.toInt()
        val idbarang = intent.getStringExtra("idproduk")
        viewModelProductSeller.getRequiredHistory(idbarang!!.toInt(),orderId)
        viewModelProductSeller.dataRequried.observe(this){
            val tujuanRek: String
            val namaRek: String
            if (it.payment_code != null && it.payment_code.isNotEmpty()) {
                tujuanRek = it.payment_code
                namaRek = it.store
            } else if (it.va_numbers != null && it.va_numbers.isNotEmpty()) {
                tujuanRek = it.va_numbers[0].va_number
                namaRek = it.va_numbers[0].bank
            } else {
                tujuanRek = it.bill_key
                namaRek = it.biller_code
            }
            apiClient.getApiService().tambahHistory(
                idUser,
                idbarang.toInt(),
                it.order_id,
                it.metadata.extra_info.user_id,
                etadress.text.toString(),
                it.transaction_time,
                it.nama_produk,
                hargabarang.toString(),
                it.gross_amount,
                etJumlah.text.toString(),
                it.gambar,
                selectedOngkos.harga,
                tujuanRek,
                namaRek
            ).enqueue(object : Callback<GetHistoryItem> {
                override fun onResponse(
                    call: Call<GetHistoryItem>,
                    response: Response<GetHistoryItem>
                ) {
                    if (response.isSuccessful){
                        startActivity(Intent(this@PaymentMidtransActivty, NotifikasiBuyerActivity::class.java))
                    }
                }
                override fun onFailure(call: Call<GetHistoryItem>, t: Throwable) {
                    Log.e("respone",t.message.toString())
                }
            })
        }
}

    @SuppressLint("SetTextI18n")
    private fun viewModel(){
        val viewModel = ViewModelProvider(this)[ViewModelHome::class.java]
        val idbarang = intent.getStringExtra("idproduk")
        produkid = idbarang!!.toInt()
        viewModel.getProductid(idbarang.toInt())
        viewModel.productid.observe(this@PaymentMidtransActivty) {
            if (it != null) {
                beratbarang = it.berat.toDouble()
                hargabarang = it.harga.toInt()
                namabarang = it.nama_produk
                val requestOptions = RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .override(400,350)
                    .skipMemoryCache(true)
                Glide.with(this)
                    .load(it.gambar)
                    .apply(requestOptions)
                    .into(produk_image)
                nama_produk.text = "Nama Produk : "+it.nama_produk
                harga_produk.text = "Harga Produk : Rp."+it.harga
                berat_produk.text = "Berat Produk :"+it.berat
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
                        addHistory()
                        Toast.makeText(this, "Success transaction", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, NotifikasiBuyerActivity::class.java))
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