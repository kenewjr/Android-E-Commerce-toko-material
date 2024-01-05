package com.example.myapplication.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.datastore.UserManager
import com.example.myapplication.model.ResponseLogin
import com.example.myapplication.network.ApiClient
import com.example.myapplication.viewmodel.ViewModelProductSeller
import com.example.myapplication.viewmodel.ViewModelUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.DelicateCoroutinesApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@DelicateCoroutinesApi
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var apiClient: ApiClient
    private lateinit var userManager: UserManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        apiClient = ApiClient()
        userManager = UserManager(this)
        login_btnLogin.setOnClickListener {
            loginauth(login_email.text.toString(),login_pass.text.toString())
        }
        login_regsister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
        imageBack.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        tv_lupaPassword.setOnClickListener {
            resetPassword()
        }
    }
    private fun resetPassword() {
        val viewModelAkun = ViewModelProvider(this)[ViewModelUser::class.java]
        val dialogView = LayoutInflater.from(this).inflate(R.layout.customdialog_editctgy, null)
        val dialogbuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Get references to views in the custom dialog
        val buttonUpdate = dialogView.findViewById<Button>(R.id.btn_editCtgy)
        val tv1 = dialogView.findViewById<TextView>(R.id.tv_komentar)
        val edt1 = dialogView.findViewById<EditText>(R.id.cd_edt_ctgy)
        tv1.text = "Reset Password"
        edt1.hint = "Contoh : tbcibeber@gmail.com"
        buttonUpdate.text = "Reset Password"
        buttonUpdate.setOnClickListener {
            val editTextname = dialogView.findViewById<EditText>(R.id.cd_edt_ctgy)
            viewModelAkun.lupaPasswordEmail(editTextname.text.toString())
            dialogbuilder.dismiss()
            Toast.makeText(applicationContext, "Password berhasil di reset silahkan cek email anda", Toast.LENGTH_SHORT).show()
        }
        // Show the custom dialog
        dialogbuilder.setCancelable(true)
        dialogbuilder.show()
    }
    private fun loginauth(loginusername : String, loginPassword : String){
        apiClient.getApiService().login(username = loginusername, password = loginPassword)
            .enqueue(object : Callback<ResponseLogin> {
                override fun onResponse(
                    call: Call<ResponseLogin>,
                    response: Response<ResponseLogin>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@LoginActivity, "Berhasil Login", Toast.LENGTH_SHORT).show()
                            userManager.setBooleanValue(true)
                            userManager.saveId(
                                id = response.body()!!.payload.id
                            )
                            userManager.savedata(
                                username = loginusername,
                                password = loginPassword,
                                status =  response.body()!!.payload.status
                            )
                        startActivity(Intent(applicationContext, HomeActivity::class.java))
                        finish()
                    } else if (login_email.text.toString().isEmpty()){
                        Toast.makeText(applicationContext, "username Harus Diisi", Toast.LENGTH_SHORT).show()
                    } else if (login_pass.text.toString().isEmpty()){
                        Toast.makeText(applicationContext, "Password Harus Diisi", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(applicationContext, "Username atau Password Salah", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_SHORT).show()
                }

            })
    }

}