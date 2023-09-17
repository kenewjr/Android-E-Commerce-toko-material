@file:Suppress("MemberVisibilityCanBePrivate", "CanBeVal",  "DEPRECATION"
)

package com.example.myapplication.view.seller


import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.datastore.UserManager
import com.example.myapplication.view.AkunsayaActivty
import com.example.myapplication.viewmodel.ViewModelProductSeller
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_edit_product.*
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
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private lateinit var postCategory: String
    var categoryID = mutableListOf<Int>()
    var categoryName = mutableListOf<String>()
    var selectedName: MutableList<String?> = mutableListOf()
    var selectedID: MutableList<Int> = mutableListOf()
    private var idproduk : Int = 0
    private var selectedUri: Uri? = null
    private lateinit var image : Uri
    private var ngambil : Boolean = false
    private var categorystatus : Boolean = false

    private val galleryResult = registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
        icon_foto.setImageURI(result)
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
            startActivity(Intent(this, AkunsayaActivty::class.java))
        }
        runOnUiThread {
            getCategory()
            arrayAdapter =
                ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categoryName)
            edt_select_kategori.setAdapter(arrayAdapter)
            edt_select_kategori.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
            arrayAdapter.notifyDataSetChanged()
            edt_select_kategori.setOnItemClickListener { _, _, position, _ ->
                val selected: String? = arrayAdapter.getItem(position)
                selectedName.add(arrayAdapter.getItem(position))
                selectedID.add(categoryID[position])
                categoryName.remove(selected)
                categoryID.remove(categoryID[position])
                val getID = selectedID.toString()
                if (getID.isNotEmpty()) {
                    categorystatus = true
                }
                postCategory = getID.replace("[", "").replace("]", "")
            }
        }
        icon_foto.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ->{
                    galleryResult.launch("image/*")
                }
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)->{
                    showPermissionContextPopup()
                }
                else -> {
                    requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1010)
                }

            }
        }

//        getdata()
//         updateproduk()
    }

//     fun updateproduk(){
//        btn_updatedataproduct.setOnClickListener {
//            val namaProdcut : RequestBody =
//                edt_namaprodut.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
//            val hargaProduct : RequestBody =
//                edt_hargaproduct.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
//            val lokasi: RequestBody =
//                edt_editlokasi.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
//            val desc : RequestBody =
//                edt_deskripsi.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
//            val viewModelSeller = ViewModelProvider(this)[ViewModelProductSeller::class.java]
//
//            AlertDialog.Builder(this)
//                .setTitle("KONFIRMASI UPDATE")
//                .setMessage("Anda Yakin Ingin Mengupdate Data Produk Ini ?")
//
//                .setPositiveButton("YA"){ _: DialogInterface, _: Int ->
//                    Toast.makeText(this, "Berhasil Diupdate", Toast.LENGTH_SHORT).show()
//                    if (!ngambil){
//                        if (!categorystatus){
//                            viewModelSeller.updateProductngnc(
//                                userManager.fetchAuthToken().toString(),
//                                idproduk,
//                                namaProdcut,
//                                desc,
//                                hargaProduct,
//                                lokasi
//                            )
//                        }else {
//                            var categoryProduct : RequestBody =
//                                postCategory.toRequestBody("text/plain".toMediaTypeOrNull())
//                            viewModelSeller.updateproduct(
//                                userManager.fetchAuthToken().toString(),
//                                idproduk,
//                                namaProdcut,
//                                desc,
//                                hargaProduct,
//                                categoryProduct,
//                                lokasi
//                            )
//                        }
//                    }else {
//                        val contentResolver = this.applicationContext.contentResolver
//                        val type = contentResolver.getType(image)
//                        val tempFile = File.createTempFile("temp-", null, null)
//                        val inputstream = contentResolver.openInputStream(image)
//                        tempFile.outputStream().use {
//                            inputstream?.copyTo(it)
//                        }
//                        val requestBody: RequestBody = tempFile.asRequestBody(type?.toMediaType())
//                        val body = MultipartBody.Part.createFormData("image", tempFile.name, requestBody)
//                        if (!categorystatus) {
//                            viewModelSeller.updateProductnc(
//                                userManager.fetchAuthToken().toString(),
//                                idproduk,
//                                namaProdcut,
//                                desc,
//                                hargaProduct,
//                                lokasi,
//                                body
//                            )
//                        } else {
//                            var categoryProduct : RequestBody =
//                                postCategory.toRequestBody("text/plain".toMediaTypeOrNull())
//                            viewModelSeller.updateproductgambar(
//                                userManager.fetchAuthToken().toString(),
//                                idproduk,
//                                namaProdcut,
//                                desc,
//                                hargaProduct,
//                                categoryProduct,
//                                lokasi,
//                                body
//                            )
//                        }
//                    }
//                    startActivity(Intent(this,DaftarJualActivity::class.java))
//                    finish()
//                }
//                .setNegativeButton("TIDAK"){ dialogInterface: DialogInterface, _: Int ->
//                    Toast.makeText(this, "Tidak Jadi Diupdate", Toast.LENGTH_SHORT).show()
//                    dialogInterface.dismiss()
//                }
//                .show()
//
//        }
//    }

    private fun startContentProvider() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 2020)

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
            else -> {
                Toast.makeText(this, "Tidak bisa mendapatkan foto.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1010 ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startContentProvider()
                } else {
                    Toast.makeText(this, "Izin DI tolak", Toast.LENGTH_SHORT).show()
                }
        }
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

//    private fun getdata(){
//        val dataProduct = intent.extras!!.getSerializable("detailorder") as GetDataProductSellerItem?
//        val viewModelSeller = ViewModelProvider(this)[ViewModelProductSeller::class.java]
//        viewModelSeller.getProductid(userManager.fetchAuthToken().toString(),dataProduct!!.id)
//        viewModelSeller.getproduk.observe(this){
//            idproduk = it.id
//            edt_namaprodut.setText(it.name)
//            edt_hargaproduct.setText(it.basePrice.toString())
//            edt_select_kategori.setText("")
//            edt_deskripsi.setText(it.desc)
//            edt_editlokasi.setText(it.location)
//            Glide.with(this).load(it.imageUrl)
//                .into(icon_foto)
//            if (it.categories.isNotEmpty()){
//                for (i in it.categories.indices){
//                    if (it.categories.lastIndex == 0){
//                        edt_select_kategori.setText(it.categories[i].name+ ",")
//                        break
//                    }
//                    if (i == 0) {
//                        edt_select_kategori.setText(it.categories[i].name + ",")
//                    } else if (i != it.categories.lastIndex && i > 0){
//                        edt_select_kategori.setText(it.categories[i].name+
//                                edt_select_kategori.text.toString() +
//                                dataProduct.categories[i].name  +
//                                ",")
//
//                    } else {
//                        edt_select_kategori.setText(edt_select_kategori.text.toString() +
//                                it.categories[i].name+ ",")
//                    }
//                }
//            }
//        }
//    }
}