package com.example.myabsensi.pojo

import java.sql.Timestamp

data class Absensi(
    val id : Int,
    val created_at : Timestamp,
    val jam_masuk : String,
    val status_masuk : String,
    val jam_pulang : String,
    val status_pulang : String,
    val user : User,
)