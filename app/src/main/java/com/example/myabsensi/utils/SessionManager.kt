package com.example.myabsensi.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(val context: Context) {

    private val PREFS_NAME = "absenSM"
    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun save(KEY_NAME: String, text: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(KEY_NAME, text).apply()
    }

    fun save(KEY_NAME: String, value: Int) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putInt(KEY_NAME, value).apply()
    }

    fun save(KEY_NAME: String, status: Boolean) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean(KEY_NAME, status).apply()
    }

    fun getValueString(KEY_NAME: String): String? {
        return sharedPref.getString(KEY_NAME, null)
    }

    fun getValueInt(KEY_NAME: String): Int {
        return sharedPref.getInt(KEY_NAME, 0)
    }

    fun getValueBoolean(KEY_NAME: String): Boolean {
        return sharedPref.getBoolean(KEY_NAME, false)
    }

    fun clearSharedPreference() {
        val edt: SharedPreferences.Editor = sharedPref.edit()

        edt.clear()
        edt.apply()
    }

    fun removeKey(KEY_NAME: String) {
        val edt: SharedPreferences.Editor = sharedPref.edit()

        edt.remove(KEY_NAME)
        edt.apply()
    }
}
