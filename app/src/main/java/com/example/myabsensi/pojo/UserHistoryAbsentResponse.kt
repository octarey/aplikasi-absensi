package com.example.myabsensi.pojo

data class UserHistoryAbsentResponse(
    val success : Boolean,
    val message : String,
    val data : List<Absensi>,
)
