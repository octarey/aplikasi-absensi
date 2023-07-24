package com.example.myabsensi.pojo

import java.sql.Timestamp

data class Attendance(
    val user_id : Int,
    val creted_at : Timestamp,
    val jam_masuk : String,
    val status_masuk : String,
    val jam_pulang : String,
    val lokasi_pulang : Int,
    val status_pulang : String,
    val image_pulang : String,

    val user_detail : User,
)
