@file:Suppress("MemberVisibilityCanBePrivate", "CanBeVal",  "DEPRECATION"
)

package com.example.myapplication.view.seller


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.datastore.UserManager
import com.example.myapplication.model.GetAllProdukItem
import com.example.myapplication.model.GetCategorySellerItem
import com.example.myapplication.viewmodel.ViewModelHome
import com.example.myapplication.viewmodel.ViewModelProductSeller
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_lengkapi_detail_product.*
import kotlinx.android.synthetic.main.custom_edit_product.*
import kotlinx.android.synthetic.main.custom_edit_product.edt_beratproduk
import kotlinx.android.synthetic.main.custom_edit_product.edt_deskripsi
import kotlinx.android.synthetic.main.custom_edit_product.edt_hargaproduct
import kotlinx.android.synthetic.main.custom_edit_product.edt_lokasi
import kotlinx.android.synthetic.main.custom_edit_product.edt_namaprodut
import kotlinx.android.synthetic.main.custom_edit_product.icon_foto
import kotlinx.android.synthetic.main.custom_edit_product.select_kategori
import kotlinx.android.synthetic.main.custom_edit_product.view.*
import kotlinx.coroutines.DelicateCoroutinesApi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@DelicateCoroutinesApi
@AndroidEntryPoint
class EditProduct : AppCompatActivity() {

    private lateinit var  userManager: UserManager
    private var kategoriproduk = ""
    private var idproduk : Int = 0
    private var selectedUri: Uri? = null
    private var ngambil : Boolean = false
    private lateinit var image : Uri
    private lateinit var selectedCategory: GetCategorySellerItem
    companion object {
        private const val REQUEST_BROWSE_PICTURE = 11
        private const val PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 22
    }
    private val galleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            icon_foto.setImageURI(result)
            selectedUri = result
            image = result!!
            ngambil = true
        }
    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_edit_product)
        userManager = UserManager(this)
        backkejual.setOnClickListener {
            startActivity(Intent(this, DaftarJualActivity::class.java))
        }
        runOnUiThread {
            getdata()
            getCategory()
        }
        icon_foto.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    galleryResult.launch("image/*")
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    showPermissionContextPopup()
                }
                else -> {
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        1010
                    )
                }
            }
        }
        updateproduk()
    }

     @SuppressLint("Recycle")
     fun updateproduk() {
         btn_updatedataproduct.setOnClickListener {
             var categoryProduct : RequestBody =
                 selectedCategory.id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
             val namaProdcut : RequestBody =
                 edt_namaprodut.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
             val hargaProduct : RequestBody =
                 edt_hargaproduct.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
             val stok: RequestBody =
                 edt_lokasi.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
             val desc : RequestBody =
                 edt_deskripsi.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
             val berat : RequestBody =
                 edt_beratproduk.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            Log.e("kategori", categoryProduct.toString())
            Log.e("pilkategori",  selectedCategory.id.toString())
             val viewModelSeller = ViewModelProvider(this)[ViewModelProductSeller::class.java]
             AlertDialog.Builder(this)
                 .setTitle("KONFIRMASI UPDATE")
                 .setMessage("Anda Yakin Ingin Mengupdate Data Produk Ini ?")

                 .setPositiveButton("YA") { _: DialogInterface, _: Int ->
                     Toast.makeText(this, "Berhasil Diupdate", Toast.LENGTH_SHORT).show()
                     if(ngambil){
                         val contentResolver = this.applicationContext.contentResolver
                         val type = contentResolver.getType(image)
                         val tempFile = File.createTempFile("temp-", null, null)
                         val inputstream = contentResolver.openInputStream(image)
                         tempFile.outputStream().use {
                             inputstream?.copyTo(it)
                         }
                         val requestBody: RequestBody = tempFile.asRequestBody(type?.toMediaType())
                         val body = MultipartBody.Part.createFormData("gambar", tempFile.name, requestBody)
                         viewModelSeller.editProduct(idproduk.toString().toRequestBody("text/plain".toMediaTypeOrNull()),namaProdcut,categoryProduct,desc,stok,hargaProduct,berat,body)
                     }else {

                         viewModelSeller.editProductNG(idproduk.toString().toRequestBody("text/plain".toMediaTypeOrNull()),namaProdcut,categoryProduct,desc,stok,hargaProduct,berat)
                     }

                     startActivity(Intent(this, DaftarJualActivity::class.java))
                     finish()
                 }
                 .setNegativeButton("TIDAK") { dialogInterface: DialogInterface, _: Int ->
                     Toast.makeText(this, "Tidak Jadi Diupdate", Toast.LENGTH_SHORT).show()
                     dialogInterface.dismiss()
                 }
                 .show()
         }
     }

    private fun createImageBrowsingRequest() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            REQUEST_BROWSE_PICTURE
        )
    }
    @SuppressLint("NewApi")
    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("izin diperlukan.")
            .setMessage("Diperlukan untuk mengimpor foto.")
            .setPositiveButton("setuju") { _, _ ->
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1010)
            }
            .create()
            .show()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            2020 -> {
                val uri = data?.data
                if (uri != null) {
                    selectedUri = uri
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    //access to gallery is allowed
                    createImageBrowsingRequest()
                } else {
                    Toast.makeText(
                        this,
                        "Permission required, to browse images",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
    private fun getCategory(){
        val viewModelSellerCategory = ViewModelProvider(this)[ViewModelProductSeller::class.java]
        viewModelSellerCategory.sellerCategory.observe(this){ it ->
            val categoryNames = it.map { it.name }.toMutableList()

            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            select_kategori.adapter = adapter

            var selectedIndex = -1
            for (i in 0 until categoryNames.size) {
                if (categoryNames[i] == kategoriproduk) {
                    selectedIndex = i
                    break // Keluar dari loop setelah menemukan pertandingan pertama
                }
            }
            Log.e("sadsadsa",selectedIndex.toString())
            select_kategori.setSelection(selectedIndex)
            select_kategori.onItemSelectedListener  = object  : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    selectedCategory = it[p2]
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    //
                }
            }
        }
        viewModelSellerCategory.getSellerCategory()
    }

    private fun getdata(){
        val dataProduct = intent.extras!!.getSerializable("detailorder") as GetAllProdukItem?
        val viewModelSeller = ViewModelProvider(this)[ViewModelHome::class.java]
        viewModelSeller.getProductid(dataProduct!!.id.toInt())
        val requestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .override(300,300)
            .skipMemoryCache(true)
        viewModelSeller.productid.observe(this){
            idproduk = it.id.toInt()
            edt_namaprodut.setText(it.nama_produk)
            edt_hargaproduct.setText(it.harga)
            edt_deskripsi.setText(it.deskripsi)
            edt_lokasi.setText(it.stok)
            edt_beratproduk.setText(it.berat)
            kategoriproduk = it.kategori
            Glide.with(this)
                .load(it.gambar)
                .apply(requestOptions)
                .into(icon_foto)
                }
            }

}