package com.example.myapplication.view

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.example.myapplication.BuildConfig
import com.example.myapplication.R
import com.example.myapplication.datastore.UserManager
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_splash.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException



@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var userManager: UserManager
    private val MY_REQUEST_CODE = 1234
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        userManager = UserManager(this)
        val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnected == true
        if (isConnected){
            runOnUiThread {
                checkForUpdate()
        }
        }else{
            Toast.makeText(applicationContext, "Tidak Ada Koneksi Internet", Toast.LENGTH_SHORT)
                .show()
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
        request.setTitle("Unduh Pembaruan Aplikasi")
        request.setDescription("Mengunduh versi terbaru aplikasi")
        request.setDestinationInExternalFilesDir(
            this,
            Environment.DIRECTORY_DOWNLOADS,
            "update.apk"
        )

        // Memulai pengunduhan
        val downloadId = downloadManager.enqueue(request)
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
    fun checkForUpdate() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.github.com/repos/kenewjr/Android-E-Commerce-toko-material/releases/latest")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Tangani kesalahan permintaan
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    val jsonObject = JSONObject(responseData)
                    val latestVersion = jsonObject.getString("tag_name")
                    val asset = jsonObject.getString("assets")
                    val browse = JSONArray(asset)
                    val link = browse.getJSONObject(0)
                    var linkdl = link.getString("browser_download_url")
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
                                    checkAccount()
                                }
                                .setNegativeButton("Nanti") { _, _ ->
                                    // Tindakan ketika pengguna mengklik "Nanti" (tutup dialog)
                                    checkAccount()
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

    fun parseVersionString(version: String): List<Int> {
        val parts = version.replace("v", "").split(".").mapNotNull {
            try {
                it.toInt()
            } catch (e: NumberFormatException) {
                null // Mengabaikan bagian yang tidak dapat diurai menjadi angka
            }
        }
        return parts
    }


    private fun checkAccount(){
        userManager = UserManager(this)
        Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
        },4500)
    }
    override fun onDestroy() {
        super.onDestroy()
        // Melepaskan broadcast receiver saat aktivitas dihancurkan
        unregisterReceiver(downloadReceiver)
    }
    }
