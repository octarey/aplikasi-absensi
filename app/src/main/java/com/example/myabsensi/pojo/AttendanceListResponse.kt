package com.example.myabsensi.pojo

data class AttendanceListResponse(
    val success : Boolean,
    val data : List<Attendance>,
    val message : String
)
