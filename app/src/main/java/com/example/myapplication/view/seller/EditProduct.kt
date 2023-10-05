@file:Suppress("MemberVisibilityCanBePrivate", "CanBeVal",  "DEPRECATION"
)

package com.example.myapplication.view.seller


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.datastore.UserManager
import com.example.myapplication.model.GetAllProdukItem
import com.example.myapplication.model.GetCategorySellerItem
import com.example.myapplication.view.AkunsayaActivty
import com.example.myapplication.viewmodel.ViewModelHome
import com.example.myapplication.viewmodel.ViewModelProductSeller
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_lengkapi_detail_product.*
import kotlinx.android.synthetic.main.custom_edit_product.*
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

@DelicateCoroutinesApi
@AndroidEntryPoint
class EditProduct : AppCompatActivity() {

    private lateinit var  userManager: UserManager
    private var kategoriproduk = ""
    private var idproduk : Int = 0
    private var selectedUri: Uri? = null
    private lateinit var image : Uri
    private var ngambil : Boolean = false
    private var categorystatus : Boolean = false
    var encodeImageString: String = ""
    private lateinit var selectedCategory: GetCategorySellerItem
    companion object {
        private const val REQUEST_BROWSE_PICTURE = 11
        private const val PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 22
        private const val MY_SHARED_PREFS = "MySharedPrefs"
        lateinit var documentImage : Bitmap
        lateinit var numberDoccument : String
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
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                createImageBrowsingRequest()
            } else {
                // Jika izin belum diberikan, tampilkan dialog permintaan izin
                showPermissionContextPopup()
            }
        }
        updateproduk()
    }

     fun updateproduk() {
         btn_updatedataproduct.setOnClickListener {
             var categoryProduct : String = selectedCategory.id.toString()
             val namaProdcut : String = edt_namaprodut.text.toString()
             val hargaProduct : String = edt_hargaproduct.text.toString()
             val stok: String = edt_lokasi.text.toString()
             val desc : String = edt_deskripsi.text.toString()
             val viewModelSeller = ViewModelProvider(this)[ViewModelProductSeller::class.java]

             AlertDialog.Builder(this)
                 .setTitle("KONFIRMASI UPDATE")
                 .setMessage("Anda Yakin Ingin Mengupdate Data Produk Ini ?")

                 .setPositiveButton("YA") { _: DialogInterface, _: Int ->
                     Toast.makeText(this, "Berhasil Diupdate", Toast.LENGTH_SHORT).show()
                     viewModelSeller.editProduct(idproduk,namaProdcut,categoryProduct,desc,stok,hargaProduct,encodeImageString)
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
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1010)
            }
            .create()
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
                Log.e(ContentValues.TAG, "Error compressing bitmap: ${ex.message}")
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
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
        viewModelSeller.productid.observe(this){
            idproduk = it.id.toInt()
            edt_namaprodut.setText(it.nama_produk)
            edt_hargaproduct.setText(it.harga)
            edt_deskripsi.setText(it.deskripsi)
            edt_lokasi.setText(it.stok)
            kategoriproduk = it.kategori
            Glide.with(this).load(it.gambar)
                .into(icon_foto)
                }
            }
    fun encodeBitmapImage(bitmap: Bitmap) {
        Log.d(ContentValues.TAG, "encodeBitmapImage called")
        if (bitmap != null && !bitmap.isRecycled) {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 70, byteArrayOutputStream)
            val bytesOfImage = byteArrayOutputStream.toByteArray()
            encodeImageString = Base64.encodeToString(bytesOfImage, Base64.DEFAULT)
        } else {
            Log.e(ContentValues.TAG, "Bitmap is null or recycled")
        }
    }
}