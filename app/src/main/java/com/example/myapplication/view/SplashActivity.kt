@file:Suppress("DEPRECATION")

package com.example.myapplication.view

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.BuildConfig
import com.example.myapplication.R
import com.example.myapplication.datastore.UserManager
import com.example.myapplication.model.GetHistoryItem
import com.example.myapplication.model.ResponseLogin
import com.example.myapplication.network.ApiClient
import com.example.myapplication.viewmodel.ViewModelProductSeller
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_notifikasi_buyer.*
import kotlinx.android.synthetic.main.activity_splash.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import kotlin.system.exitProcess


@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var userManager: UserManager
    var latest :String = ""
    private lateinit var apiClient: ApiClient
    // Mendefinisikan broadcast receiver untuk menangani penyelesaian unduhan
    private val downloadReceiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
                val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (downloadId != -1L) {
                    val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    val query = DownloadManager.Query().setFilterById(downloadId)
                    val cursor = downloadManager.query(query)
                    if (cursor.moveToFirst()) {
                        val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        val status = cursor.getInt(columnIndex)
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            val uri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                            if (uri != null) {
                                installApp(uri)
                            }
                        } else {
                            // Unduhan gagal, tindakan yang sesuai dapat diambil di sini
                        }
                    }
                    cursor.close()
                }
            }
        }
    }
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        userManager = UserManager(this)
        apiClient = ApiClient()
        val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnected == true
        val booleanvalue = userManager.getBooleanValue()
        if (isConnected){
            runOnUiThread {
                checkForUpdate()
                when {
                    booleanvalue -> {
                        notif()
                    }
                }
        }
        }else{
            Toast.makeText(applicationContext, "Tidak Ada Koneksi Internet", Toast.LENGTH_SHORT)
                .show()
            textView22.text = "Tidak Ada Koneksi Internet"
        }
        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        registerReceiver(downloadReceiver, filter)

        textView5.isVisible = true
        progressBarSplash.isVisible = true
    }
    private fun startAppUpdate(link : String) {
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        // Buat permintaan pengunduhan
        val downloadUri = Uri.parse(link)
        val request = DownloadManager.Request(downloadUri)
        request.setTitle("TBCibeberKencana${latest}.apk")
        request.setDescription("Mengunduh versi terbaru aplikasi")
        request.setDestinationInExternalFilesDir(
            this,
            Environment.DIRECTORY_DOWNLOADS,
            "TBCibeberKencana${latest}.apk"
        )

        // Memulai pengunduhan
        downloadManager.enqueue(request)
    }

    private fun notif() {
        Log.e("notif",userManager.fetchstatus().toString())
        if (userManager.fetchstatus() == "seller") {
            val viewModelProductSeller = ViewModelProvider(this)[ViewModelProductSeller::class.java]
            viewModelProductSeller.getHistory()
            viewModelProductSeller.datahistory.observe(this) {
                if (it.isNotEmpty()) {
                    val statusLunasItems = it.filter { item -> item.status == "Lunas" }
                    val namaProduk = statusLunasItems.map { it.nama_produk }
                    if (statusLunasItems.isNotEmpty()) {
                        Log.e("seller","uwu")
                        showNotification(this, "Ada Pesanan", "Segera Selesaikan Pesanan $namaProduk")
                    }
                }
            }
        }else {
            apiClient.getApiService().getHistoryUserID(userManager.fetchId()!!.toInt())
                .enqueue(object : retrofit2.Callback<List<GetHistoryItem>> {
                    override fun onResponse(
                        call: retrofit2.Call<List<GetHistoryItem>>,
                        response: retrofit2.Response<List<GetHistoryItem>>
                    ) {
                        if (response.isSuccessful) {
                            Log.e("buyer","uwu2")
                            val status = response.body()!!
                            val statusPending = status.filter { items -> items.status == "pending" }
                            val namaProduk = statusPending.map { it.nama_produk }
                            if (statusPending.isNotEmpty()) {
                                showNotification(this@SplashActivity, "Selesaikan Pembayaran", "Segera Selesaikan Pembayaran $namaProduk.")
                            }
                        }
                    }
                    override fun onFailure(call: retrofit2.Call<List<GetHistoryItem>>, t: Throwable) {
                        Log.e("error",t.message.toString())
                    }
                })
        }
    }
    private fun installApp(uriString: String) {
        val uri = Uri.parse(uriString)
        val installIntent = Intent(Intent.ACTION_VIEW)
        installIntent.setDataAndType(uri, "application/vnd.android.package-archive")
        installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        installIntent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
        installIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true)
        val file = File(uri.path!!)
        val contentUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                this@SplashActivity,
                "${packageName}.fileprovider",
                file
            )
        } else {
            Uri.fromFile(file)
        }
        installIntent.data = contentUri
        startActivity(installIntent)
    }
    private fun checkForUpdate() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.github.com/repos/kenewjr/Android-E-Commerce-toko-material/releases/latest")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Tangani kesalahan permintaan
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    val jsonObject = JSONObject(responseData!!)
                    val latestVersion = jsonObject.getString("tag_name")
                    latest = latestVersion
                    val asset = jsonObject.getString("assets")
                    val browse = JSONArray(asset)
                    val link = browse.getJSONObject(0)
                    val linkdl = link.getString("browser_download_url")
                    // Bandingkan dengan versi saat ini
                    val currentVersion = BuildConfig.VERSION_NAME

                    if (compareVersions(latestVersion, currentVersion)) {
                        // Ada pembaruan tersedia
                        runOnUiThread {
                            val dialog = AlertDialog.Builder(this@SplashActivity)
                                .setTitle("Pembaruan Tersedia")
                                .setMessage("Versi terbaru aplikasi telah tersedia. Apakah Anda ingin mengunduh pembaruan sekarang?")
                                .setPositiveButton("Ya") { _, _ ->
                                    // Tindakan ketika pengguna mengklik "Ya" (unduh pembaruan)
                                    // Panggil metode untuk mengunduh pembaruan aplikasi di sini
                                    startAppUpdate(linkdl)
                                    textView22.text = "Update Tersedia Silahkan Install Terlebih Dahulu \n"+"Jangan Tutup Aplikasi Sampai Instalasi Selesai"
                                }
                                .setNegativeButton("Nanti") { _, _ ->
                                    // Tindakan ketika pengguna mengklik "Nanti" (tutup dialog)
                                    exitApp()
                                }
                                .create()

                            dialog.show()
                        }
                    }else {
                        checkAccount()
                    }
                }
            }
        })
    }

    fun showNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(context, "channel_id")
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationId = 1 // Set ID notifikasi sesuai kebutuhan Anda
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
    private fun exitApp() {
        finish() // This closes the current activity
        exitProcess(0) // This forcefully terminates the app (use with caution)
    }
    fun compareVersions(latestVersion: String, currentVersion: String): Boolean {
        val latestParts = parseVersionString(latestVersion)
        val currentParts = parseVersionString(currentVersion)

        if (latestParts.isEmpty() || currentParts.isEmpty()) {
            return false // Format versi tidak valid
        }

        for (i in 0 until minOf(latestParts.size, currentParts.size)) {
            if (latestParts[i] > currentParts[i]) {
                return true // latestVersion lebih baru
            } else if (latestParts[i] < currentParts[i]) {
                return false // currentVersion lebih baru
            }
        }

        // Jika kode mencapai sini, versi sama atau currentVersion lebih baru
        return false
    }

    private fun parseVersionString(version: String): List<Int> {
        val parts = version.replace("v", "").split(".").mapNotNull {
            try {
                it.toInt()
            } catch (e: NumberFormatException) {
                null // Mengabaikan bagian yang tidak dapat diurai menjadi angka
            }
        }
        return parts
    }

    private fun loginauth(loginusername : String, loginPassword : String){
        apiClient.getApiService().login(username = loginusername, password = loginPassword)
            .enqueue(object : retrofit2.Callback<ResponseLogin> {
                override fun onResponse(
                    call: retrofit2.Call<ResponseLogin>,
                    response: retrofit2.Response<ResponseLogin>
                ) {
                    if (response.isSuccessful) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
                            Toast.makeText(this@SplashActivity, "Selamat Datang Kembali", Toast.LENGTH_SHORT).show()
                            finish()
                        },4500)
                    }  else {
                        Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(Intent(applicationContext, LoginActivity::class.java))
                            Toast.makeText(applicationContext, "Username atau Password Salah", Toast.LENGTH_SHORT).show()
                            finish()
                        },4500)
                        userManager.setBooleanValue(false)
                        userManager.logout()
                    }
                }

                override fun onFailure(call: retrofit2.Call<ResponseLogin>, t: Throwable) {
                    Toast.makeText(this@SplashActivity, t.message, Toast.LENGTH_SHORT).show()
                }

            })
    }
    private fun checkAccount(){
        userManager = UserManager(this)
        if (userManager.getBooleanValue() == false) {
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            },4500)
        }else{
            loginauth(userManager.fetchusername().toString(),userManager.fetchpassword().toString())
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        // Melepaskan broadcast receiver saat aktivitas dihancurkan
        unregisterReceiver(downloadReceiver)
    }
    }
