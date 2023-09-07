@file:Suppress("CanBeVal",  "DEPRECATION", "RedundantOverride", "MemberVisibilityCanBePrivate"
)

package com.example.myapplication.view.seller

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.datastore.UserManager
import com.example.myapplication.view.HomeActivity
import com.example.myapplication.viewmodel.ViewModelProductSeller
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_lengkapi_detail_product.*
import kotlinx.android.synthetic.main.customdialog_preview.view.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

@DelicateCoroutinesApi
@AndroidEntryPoint
class LengkapiDetailProductActivity : AppCompatActivity() {
    private var selectedUri: Uri? = null
    var encodeImageString: String = ""
    companion object {
        private const val REQUEST_BROWSE_PICTURE = 11
        private const val PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 22
        private const val MY_SHARED_PREFS = "MySharedPrefs"
        lateinit var documentImage : Bitmap
        lateinit var numberDoccument : String
    }
    private lateinit var userManager: UserManager
    private lateinit var image: Uri

    private lateinit var arrayAdapter: ArrayAdapter<String>
    private lateinit var postCategory: String
    var categoryID = mutableListOf<Int>()
    var categoryName = mutableListOf<String>()
    var selectedName: MutableList<String?> = mutableListOf()
    var selectedID: MutableList<Int> = mutableListOf()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lengkapi_detail_product)
        userManager = UserManager(this)
        getCategory()
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categoryName)
        select_kategori.setAdapter(arrayAdapter)
        select_kategori.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
        arrayAdapter.notifyDataSetChanged()
        select_kategori.setOnItemClickListener { _, _, position, _ ->
            val selected: String? = arrayAdapter.getItem(position)
            selectedName.add(arrayAdapter.getItem(position))
            selectedID.add(categoryID[position])
            categoryName.remove(selected)
            categoryID.remove(categoryID[position])
            val getID = selectedID.toString()
            postCategory = getID.replace("[", "").replace("]", "")
        }
        back.setOnClickListener {
            startActivity(Intent(this,DaftarJualActivity::class.java) )
        }
        btn_terbitkan.setOnClickListener {
            jualbarang()
        }
//        btn_preview.setOnClickListener {
//            GlobalScope.launch {
//                userManager.clearPreview()
//            }
//            preview()
//            getPreview()
//      }

        icon_foto.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                createImageBrowsingRequest()
            } else {
                // Jika izin belum diberikan, tampilkan dialog permintaan izin
                showPermissionContextPopup()
            }
        }

    }
//    fun getPreview(){
//        userManager = UserManager(this)
//        val viewModelDataSeller = ViewModelProvider(this)[ViewModelProductSeller::class.java]
//        viewModelDataSeller.getSeller(token = userManager.fetchAuthToken().toString())
//        val dialogView = layoutInflater.inflate(R.layout.customdialog_preview, null)
//        val dialogbuilder = AlertDialog.Builder(this).setView(dialogView).create()
//        val btnpreview = dialogView.addProductSeller_btnTerbit
//        viewModelDataSeller.seller.observe(this) {
//            dialogView.TV_nama.text = "Nama Seller" + it.fullName
//            dialogView.seller_kota.text = "Kota Seller" + it.city
//            Glide.with(dialogView.IV_penjual.context).load(it.imageUrl).into(dialogView.IV_penjual)
//        }
//        userManager.harga.asLiveData().observe(this){
//            dialogView.addProduct_harga.text = "Rp.$it"
//        }
//        userManager.name.asLiveData().observe(this){
//            dialogView.addProduct_namaproduk.text = "Nama Produk : $it"
//        }
//        userManager.deskripsi.asLiveData().observe(this){
//            dialogView.addProduct_deskripsi.text = it
//        }
//        userManager.kategori.asLiveData().observe(this){
//            dialogView.addProduct_category.text = it
//        }
//        userManager.gambar.asLiveData().observe(this){
//            Glide.with(dialogView.add_gambar.context).load(it).into(dialogView.add_gambar)
//        }
//        btnpreview.setOnClickListener {
//            jualbarang()
//            dialogbuilder.dismiss()
//        }
//        dialogbuilder.setCancelable(true)
//        dialogbuilder.show()
//    }
//    fun preview() {
//        val namaproduk = edt_namaprodut.text.toString()
//        val lokasi = edt_lokasi.text.toString()
//        val categoryproduk = selectedName
//        val hargaproduk = edt_hargaproduct.text.toString()
//        val deskripsi = edt_deskripsi.text.toString()
//        if (selectedUri != null) {
//            val photoUri = selectedUri
//            uploadPhoto(photoUri!!,
//                successHandler = { uri ->
//                    GlobalScope.launch {
//                        userManager.preview(
//                            namaproduk,
//                            hargaproduk,
//                            categoryproduk.toString(),
//                            deskripsi,
//                            lokasi,
//                            uri
//                        )
//                    }
//                },
//                errorHandler = {
//                    Toast.makeText(this, "Gagal.", Toast.LENGTH_SHORT).show()
//                }
//            )
//        }
//    }
//
    fun jualbarang(){
        var categoryProduct : String = postCategory
        val namaProdcut : String = edt_namaprodut.text.toString()
        val hargaProduct : String = edt_hargaproduct.text.toString()
        val stok: String = edt_lokasi.text.toString()
        val desc : String = edt_deskripsi.text.toString()
        val viewModelDataSeller = ViewModelProvider(this)[ViewModelProductSeller::class.java]
        viewModelDataSeller.jualproduct(namaProdcut,desc,hargaProduct,categoryProduct,stok,encodeImageString)
        startActivity(Intent(applicationContext, HomeActivity::class.java))
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
                        this@LengkapiDetailProductActivity,
                        "Permission required, to browse images",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult called with requestCode $requestCode and resultCode $resultCode")
        if (requestCode == REQUEST_BROWSE_PICTURE && resultCode == Activity.RESULT_OK) {
            val filepath: Uri? = data?.data
            try {
                val inputStream: InputStream? = contentResolver.openInputStream(filepath!!)
                val localBitmap = BitmapFactory.decodeStream(inputStream)
                localBitmap?.let {
                    documentImage = it
                    encodeBitmapImage(documentImage)
                    icon_foto.setImageBitmap(documentImage)
                }
            } catch (ex: Exception) {
                Log.e(TAG, "Error compressing bitmap: ${ex.message}")
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    fun encodeBitmapImage(bitmap: Bitmap) {
        Log.d(TAG, "encodeBitmapImage called")
        if (bitmap != null && !bitmap.isRecycled) {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 70, byteArrayOutputStream)
            val bytesOfImage = byteArrayOutputStream.toByteArray()
            encodeImageString = Base64.encodeToString(bytesOfImage, Base64.DEFAULT)
        } else {
            Log.e(TAG, "Bitmap is null or recycled")
        }
    }



    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("izin diperlukan.")
            .setMessage("Diperlukan untuk mengimpor foto.")
            .setPositiveButton("setuju") { _, _ ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1010)
                }
            }
            .create()
            .show()
    }
    private fun getCategory(){
        val viewModelSellerCategory = ViewModelProvider(this)[ViewModelProductSeller::class.java]
        viewModelSellerCategory.sellerCategory.observe(this){ it ->
            it.forEach{
                categoryName.add(it.name)
                categoryID.add(it.id)
            }
        }
        viewModelSellerCategory.getSellerCategory()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}