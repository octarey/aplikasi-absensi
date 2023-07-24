package com.example.myabsensi.utils

import android.content.Context
import android.content.SharedPreferences

class PrefManager(var context: Context) {
    // Shared pref mode
    val PRIVATE_MODE = 0

    // Sharedpref file name
    private val PREF_NAME = "SharedPreferences"

    private val IS_LOGIN = "is_login"

    var pref: SharedPreferences? = context?.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
    var editor: SharedPreferences.Editor? = pref?.edit()

    fun setLoggin(isLogin: Boolean) {
        editor?.putBoolean(IS_LOGIN, isLogin)
        editor?.commit()
    }

    fun setLogin(type:String, id:Int, absenId:Int) {
        editor?.putString("type", type)
        editor?.putInt("id", id)
        editor?.putInt("absenId", absenId)
        editor?.commit()
    }

    fun isLogin(): Boolean? {
        return pref?.getBoolean(IS_LOGIN, false)
    }

    fun getType():String? {
        return pref?.getString("type", "")
    }

    fun getToken(): String? {
        return pref?.getString("token", "")
    }

    fun getId(): Int {
        return pref?.getInt("id", 0)!!
    }
    fun removeData() {
        editor?.clear()
        editor?.commit()
    }
}