package com.example.myabsensi.utils

import android.graphics.Color
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.example.myabsensi.R
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class Helper {
    object Utils {
        fun indonesianDate(date: Date): String {
            return SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id")).format(date).toString()
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun getTime(): String {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        }

        fun erroret(msg: String, color:Int, view: View){
            val snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
            snackbar.view.minimumHeight = 20
            snackbar.view.setBackgroundResource(color)
            snackbar.setTextColor(Color.BLACK)
            snackbar.show()
        }
    }
}