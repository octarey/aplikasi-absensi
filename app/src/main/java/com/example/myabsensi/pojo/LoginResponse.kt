package com.example.myabsensi.pojo

data class LoginResponse(
    val success : Boolean,
    val message : String,
    val data : User,
)
