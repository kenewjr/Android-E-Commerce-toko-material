package com.example.myapplication.datastore

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import com.example.myapplication.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserManager(context : Context) {
    private  val dataStore: DataStore<Preferences> = context.createDataStore("login-prefs")
    private val finger : DataStore<Preferences> = context.createDataStore("fingerprint")
    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val ID = "ID"
        const val IMAGEUSER = "IMAGEUSER"
        const val KOTA = "KOTA"
        val USERNAME = preferencesKey<String>("USERNAME")
        val NAME = preferencesKey<String>("NAME")
        val HARGA = preferencesKey<String>("HARGA")
        val KATEGORI = preferencesKey<String>("KATEGORI")
        val LOKASI = preferencesKey<String>("LOKASI")
        val DESC = preferencesKey<String>("DESC")
        val IMAGE = preferencesKey<String>("IMAGE")
        val PASSWORD = preferencesKey<String>("PASSWORD")
        val BOOLEAN = preferencesKey<Boolean>("BOOLEAN")
    }
    suspend fun preview(
        nama : String,
        harga : String,
        kategori : String,
        deskripsi : String,
        lokasi : String,
        image : String
    ){
        dataStore.edit {
            it[NAME] = nama
            it[HARGA] = harga
            it[KATEGORI] = kategori
            it[LOKASI] = lokasi
            it[DESC] = deskripsi
            it[IMAGE] = image
        }
    }
    suspend fun logindata(
        username: String,
        password: String
    ){
        dataStore.edit {
            it[USERNAME] = username
            it[PASSWORD]= password
        }
    }

    suspend fun setBoolean(boolean: Boolean) {
        dataStore.edit {
            it[BOOLEAN] = boolean
        }
    }

    suspend fun clearPreview() {
        dataStore.edit {
            it.clear()
        }
    }
    fun logout(){
        val edit = prefs.edit()
        edit
            .clear()
            .apply()
    }

    fun saveId(id: String) {
        val editor = prefs.edit()
        editor.putString(ID, id)
            .apply()
    }

    fun fetchId(): String? {
        return prefs.getString(ID, null)
    }
    val ceklogin: Flow<Boolean> = dataStore.data.map {
        it[BOOLEAN] ?: false
    }

    val username : Flow<String> = dataStore.data.map {
        it[USERNAME] ?: ""
    }
}