package com.example.myabsensi.utils

import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class Helper {
    object Utils {
        fun indonesianDate(date: Date): String {
            return SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id")).format(date).toString()
        }
    }
}