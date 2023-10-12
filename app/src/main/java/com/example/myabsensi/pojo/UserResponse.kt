package com.example.myabsensi.pojo

data class UserResponse(
    val success : Boolean,
    val message : String,
    val data : List<User>,
)
