package com.example.myabsensi.pojo

data class UserHistoryAbsentResponse(
    val success : Boolean,
    val data : List<Absensi>,
    val message : String
)
