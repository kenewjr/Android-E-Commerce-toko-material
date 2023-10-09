package com.example.myapplication.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myapplication.R
import com.example.myapplication.datastore.UserManager
import com.example.myapplication.model.DataUser
import com.example.myapplication.model.LoginRequest
import com.example.myapplication.model.ResponseLogin
import com.example.myapplication.network.ApiClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@DelicateCoroutinesApi
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var apiClient: ApiClient
    private lateinit var userManager: UserManager
    private var email : String = ""
    private var password : String = ""
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
           onBackPressed()
        }
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