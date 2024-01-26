package com.example.myapplication.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.datastore.UserManager
import com.example.myapplication.model.PostRegisterUser
import com.example.myapplication.network.ApiClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.coroutines.DelicateCoroutinesApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@DelicateCoroutinesApi
@AndroidEntryPoint
class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var apiClient: ApiClient
    private lateinit var userManager: UserManager
    private var username : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        userManager = UserManager(this)
        apiClient = ApiClient()
        username = userManager.fetchusername().toString()
        changePassword()
        back_keakunsaya.setOnClickListener {
                startActivity(Intent(this,AkunsayaActivty::class.java))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun changePassword(){
        btn_change_pasword.setOnClickListener {
            if (etNewPassword.text.toString().isEmpty()){
                Toast.makeText(this, "password baru tidak boleh kosong", Toast.LENGTH_SHORT).show()
                tv_error_new_password.text = "Masukan password baru anda"
            } else if (etConfirmPassword.text.toString().isEmpty()){
                Toast.makeText(this, "konfirmasi password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                tv_error_confirm_password.text = "Masukan konfirmasi password"
            } else if (etConfirmPassword.text.toString() != etNewPassword.text.toString()){
                Toast.makeText(this, "konfirmasi password tidak sama", Toast.LENGTH_SHORT).show()
                tv_error_confirm_password.text = "Password baru & konfirmasi password harus sama"
            } else {
                prosesChangePassword()
            }
        }

    }

    private fun prosesChangePassword(){
        val newPassword : String = etNewPassword.text.toString()

        apiClient.getApiService().changePassword(
           username,newPassword
        )
            .enqueue(object : Callback<PostRegisterUser> {
                override fun onResponse(
                    call: Call<PostRegisterUser>,
                    response: Response<PostRegisterUser>
                ) {
                    if (response.isSuccessful){
                        Toast.makeText(this@ChangePasswordActivity, "Ubah Password Berhasil, Silahkan login ulang", Toast.LENGTH_SHORT).show()
                        userManager.setBooleanValue(false)
                        userManager.logout()
                        startActivity(Intent(applicationContext, LoginActivity::class.java))
                    } else {
                        Toast.makeText(this@ChangePasswordActivity, "Password Lama Salah", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PostRegisterUser>, t: Throwable) {
                    Toast.makeText(this@ChangePasswordActivity, t.message, Toast.LENGTH_SHORT).show()
                }

            })
    }
}