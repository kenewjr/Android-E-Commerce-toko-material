package com.example.myapplication.datastore

import android.content.Context
import android.preference.PreferenceManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData



class UserManager(context : Context) {
    private val prefs by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    companion object {
        const val ID = "ID"
        const val IMAGEUSER = "IMAGEUSER"
        const val KOTA = "KOTA"
        const val BOOLEAN_KEY = "my_boolean_key"
        const val USERNAME = "USERNAME"
        const val PASSWORD = "PASSWORD"
    }

    private val _booleanLiveData = MutableLiveData<Boolean>()
    fun setBooleanValue(value: Boolean) {
        prefs.edit().putBoolean(BOOLEAN_KEY, value).apply()
        _booleanLiveData.value = value // Notify LiveData observers of the change
    }

    fun getBooleanValue(): Boolean {
        return prefs.getBoolean(BOOLEAN_KEY, false) // Replace false with the default value you want
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

    fun savedata(username:String,password:String){
        val editor = prefs.edit()
        editor.putString(USERNAME,username)
        editor.putString(PASSWORD,password)
            .apply()
    }

    fun fetchusername(): String?{
        return prefs.getString(USERNAME,null)
    }
    fun fetchpassword(): String?{
        return prefs.getString(PASSWORD,null)
    }
    fun fetchId(): String? {
        return prefs.getString(ID, null)
    }
}