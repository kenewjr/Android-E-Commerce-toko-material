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
import android.view.View
import android.widget.AdapterView
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
import com.example.myapplication.model.GetCategorySellerItem
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
    private lateinit var selectedCategory: GetCategorySellerItem

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lengkapi_detail_product)
        userManager = UserManager(this)
        getCategory()

        back.setOnClickListener {
            startActivity(Intent(this,DaftarJualActivity::class.java) )
        }
        btn_terbitkan.setOnClickListener {
            jualbarang()
        }


        icon_foto.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                createImageBrowsingRequest()
            } else {
                // Jika izin belum diberikan, tampilkan dialog permintaan izin
                showPermissionContextPopup()
            }
        }

    }

    fun jualbarang(){
        var categoryProduct : String = selectedCategory.id.toString()
        val namaProdcut : String = edt_namaprodut.text.toString()
        val hargaProduct : String = edt_hargaproduct.text.toString()
        val beratProduk : String = edt_beratproduk.text.toString()
        val stok: String = edt_lokasi.text.toString()
        val desc : String = edt_deskripsi.text.toString()
        val viewModelDataSeller = ViewModelProvider(this)[ViewModelProductSeller::class.java]
        viewModelDataSeller.jualproduct(namaProdcut,desc,beratProduk,hargaProduct,categoryProduct,stok,encodeImageString)
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
        val maxFileSize = 1024 * 1024
        if (bitmap != null && !bitmap.isRecycled) {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
            val bytesOfImage = byteArrayOutputStream.toByteArray()
            if (bytesOfImage.size > maxFileSize) {
                // Gambar terlalu besar, tampilkan pesan kesalahan kepada pengguna
                Toast.makeText(this, "Ukuran gambar terlalu besar", Toast.LENGTH_SHORT).show()
                tv_warningn.text = "Ukurang Makismal Gambar Adalah 1024 x 1024"
            } else {
                // Gambar dalam batas ukuran yang diizinkan, lanjutkan mengunggahnya ke server
                encodeImageString = Base64.encodeToString(bytesOfImage, Base64.DEFAULT)
            }

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
            val categoryNames = it.map { it.name }.toMutableList()
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            select_kategori.adapter = adapter

            select_kategori.onItemSelectedListener  = object  : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    selectedCategory = it[p2]
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
        }
        viewModelSellerCategory.getSellerCategory()
    }
}