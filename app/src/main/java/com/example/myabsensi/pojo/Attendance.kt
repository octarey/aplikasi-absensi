package com.example.myabsensi.pojo

import java.sql.Timestamp

data class Attendance(
    val user_id : Int,
    val user_detail : User,
    val jam_masuk : String,
    val lokasi_masuk : Int,
    val status_masuk : String,
    val image_masuk : String,
    val jam_pulang : String,
    val lokasi_pulang : Int,
    val status_pulang : String,
    val image_pulang : String,
    val tanggal : Timestamp
)
