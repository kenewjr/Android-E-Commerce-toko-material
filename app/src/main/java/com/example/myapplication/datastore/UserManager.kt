@file:Suppress("BooleanMethodIsAlwaysInverted", "BooleanMethodIsAlwaysInverted", "DEPRECATION")

package com.example.myapplication.datastore

import android.content.Context
import android.preference.PreferenceManager
import androidx.lifecycle.MutableLiveData


@Suppress("BooleanMethodIsAlwaysInverted")
class UserManager(context : Context) {
    private val prefs by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    companion object {
        const val ID = "ID"
        const val STATUS = "STATUS"
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

    fun savedata(username:String,password:String,status:String){
        val editor = prefs.edit()
        editor.putString(USERNAME,username)
        editor.putString(STATUS,status)
        editor.putString(PASSWORD,password)
            .apply()
    }

    fun fetchusername(): String?{
        return prefs.getString(USERNAME,null)
    }
    fun fetchpassword(): String?{
        return prefs.getString(PASSWORD,null)
    }
    fun fetchstatus(): String?{
        return prefs.getString(STATUS,null)
    }

    fun fetchId(): String? {
        return prefs.getString(ID, null)
    }
}