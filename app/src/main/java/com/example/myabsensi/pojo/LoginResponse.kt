package com.example.myabsensi.pojo

data class LoginResponse(
    val success : Boolean,
    val data : User,
    val message : String,
)
