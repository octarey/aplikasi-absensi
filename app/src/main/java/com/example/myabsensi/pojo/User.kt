package com.example.myabsensi.pojo

import java.io.Serializable

data class User(
    val id : Int?,
    val name : String,
    val username : String,
    val password : String,
    val divisi : String,
    val android_id : String,
): Serializable
