package com.example.myabsensi.pojo

import java.sql.Timestamp

data class Attendance(
    val user_id : Int,
    val location: Int,
    val time : String,
    val status : String,
    val user_detail : User
)
