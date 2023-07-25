package com.example.myabsensi.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class ManagePermisions(val activity: Activity, val code: Int) {
    private final val TAG: String = "ManagePermission"
    val listPermission: List<String> = listOf<String>(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    fun checkPermission() {
        this.requestPermissions()
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(activity, listPermission.toTypedArray(), code)
    }

    // Process permissions result
    fun processPermissionsResult(requestCode: Int, grantResults: IntArray): Boolean {
        var result = 0
        if (grantResults.isNotEmpty()) {
            for (item in grantResults) {
                result += item
            }
        }
        if (result == PackageManager.PERMISSION_GRANTED) return true
        return false
    }
}