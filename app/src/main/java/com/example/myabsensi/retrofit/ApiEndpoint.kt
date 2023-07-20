package com.example.myabsensi.retrofit

import com.example.myabsensi.pojo.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiEndpoint {
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email:String,
        @Field("password") password:String
    ):Call<LoginResponse>

    @FormUrlEncoded
    @POST("user-attendance")
    fun userAttendance(
        @Field("user_id") user_id:Int,
        @Field("name") name:String,
        @Field("division") division:String,
        @Field("location") location:Int,
        @Header("Authorization") authorization: String
    ):Call<AttendanceResponse>

    @GET("list-attendance")
    fun listAttendance():Call<AttendanceListResponse>

    @GET("get-user/{id}")
    fun getUser(
        @Path("id") id:Int,
        @Header("Authorization") authorization: String
    ):Call<UserDetailResponse>

    @GET("get-absentToday/{id}")
    fun getAbsentToday(
        @Path("id") id:Int,
        @Header("Authorization") authorization: String
    ):Call<UserAbsentTodayResponse>

    @FormUrlEncoded
    @POST("absent-masuk")
    fun postAbsenMasuk(
        @Field("user_id") user_id:Int,
        @Field("lokasi_masuk") name:Int,
        @Header("Authorization") authorization: String
    ):Call<UserAbsentTodayResponse>

    @FormUrlEncoded
    @POST("absent-pulang/{id}")
    fun postAbsenPulang(
        @Path("id") id:Int,
        @Field("lokasi_pulang") name:Int,
        @Header("Authorization") authorization: String
    ):Call<UserAbsentTodayResponse>

    @GET("get-absent/{user_id}")
    fun getHistoryAbsent(
        @Path("user_id") user_id: Int,
        @Header("Authorization") authorization: String
    ):Call<UserHistoryAbsentResponse>
}