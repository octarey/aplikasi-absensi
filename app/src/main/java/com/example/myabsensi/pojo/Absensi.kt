package com.example.myabsensi.pojo

import java.sql.Timestamp

data class Absensi(
    val id : Int,
    val jam_masuk : String,
    val lokasi_masuk : Int,
    val status_masuk : String,
    val jam_pulang : String,
    val lokasi_pulang : Int,
    val status_pulang : String,
    val tanggal : Timestamp
)