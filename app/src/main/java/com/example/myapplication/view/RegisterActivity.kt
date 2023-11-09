package com.example.myapplication.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.model.PostRegisterUser
import com.example.myapplication.network.ApiClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.DelicateCoroutinesApi
import retrofit2.Call
import retrofit2.Response

@OptIn(DelicateCoroutinesApi::class)
@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private lateinit var apiClient: ApiClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        register()
        loginDisini.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)(@)(.+)(\\.)(.+)"
        return email.matches(emailRegex.toRegex())
    }
    @SuppressLint("SetTextI18n")
    private fun register(){
        btn_daftar.setOnClickListener {
            val username : String = etUsername_register.text.toString()
            val email : String = etEmail_register.text.toString()
            val nama : String = etNama_register.text.toString()
            val password : String = etPassword_register.text.toString()
            val phone : String = etPhone_register.text.toString()
            val alamat : String = etAddress_register.text.toString()
            val kota: String = etKota_register.text.toString()
            val kodepos: String = etKodepos_register.text.toString()
            if (etNama_register.text.isEmpty()){
                Toast.makeText(this@RegisterActivity, "Nama lengkap harus di isi", Toast.LENGTH_SHORT).show()
                tv_error_nama_register.text = "Nama lengkap harus di isi"
            } else if (!isValidEmail(email)){
                Toast.makeText(this@RegisterActivity, "Email harus diisi dengan benar", Toast.LENGTH_SHORT).show()
                tv_error_email_register.text = "Email harus diisi dengan benar"
            } else if ( etPassword_register.text.isEmpty()){
                Toast.makeText(this@RegisterActivity, "Password harus di isi", Toast.LENGTH_SHORT).show()
                tv_error_password_register.text = "Password harus di isi"
            } else if ( etPassword_register.text.length < 5){
                Toast.makeText(this@RegisterActivity, "Panjang Password kurang dari 5", Toast.LENGTH_SHORT).show()
                tv_error_password_register.text = "Panjang Password kurang dari 5"
            } else if (etPhone_register.text.isEmpty()){
                Toast.makeText(this@RegisterActivity, "Nomor Handphone Tidak Boleh Kosong", Toast.LENGTH_SHORT).show()
                tv_error_phone_register.text = "Nomor Handphone Harus di isi "
            }else if (etAddress_register.text.isEmpty()){
                Toast.makeText(this@RegisterActivity, "Address Tidak Boleh Kosong", Toast.LENGTH_SHORT).show()
                tv_error_address_register.text = "Address Harus di isi "
            }else if (etUsername_register.text.isEmpty()){
                Toast.makeText(this@RegisterActivity, "Username Tidak Boleh Kosong", Toast.LENGTH_SHORT).show()
                tv_error_username_register.text = "Username Harus di isi "
            }else if (etKota_register.text.isEmpty()){
                Toast.makeText(this@RegisterActivity, "Kota Tidak Boleh Kosong", Toast.LENGTH_SHORT).show()
                tv_error_kota_register.text = "Kota Harus di isi "
            }else if (etKodepos_register.text.isEmpty()){
                Toast.makeText(this@RegisterActivity, "KodePos Tidak Boleh Kosong", Toast.LENGTH_SHORT).show()
                tv_error_kodepos_register.text = "KodePos Harus di isi "
            }
            else {
                doRegister(username,nama,password,alamat,kota,kodepos,phone,email)
            }
        }
        }

    private fun doRegister(username: String, nama: String, password: String, alamat : String,kota : String,kodepos : String, phone : String,email :String){
        apiClient = ApiClient()
        apiClient.getApiService().register(username,password,nama,phone,alamat,kota,kodepos,email)
            .enqueue(object : retrofit2.Callback<PostRegisterUser> {
                override fun onResponse(
                    call: Call<PostRegisterUser>,
                    response: Response<PostRegisterUser>
                ) {
                    if (response.isSuccessful){
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        Toast.makeText(this@RegisterActivity, "Berhasil Membuat Akun", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@RegisterActivity, "Data Sudah Terdaftar", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PostRegisterUser>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }
}