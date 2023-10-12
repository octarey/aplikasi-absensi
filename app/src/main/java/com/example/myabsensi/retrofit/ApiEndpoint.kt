package com.example.myabsensi.retrofit

import com.example.myabsensi.pojo.*
import retrofit2.Call
import retrofit2.http.*

interface ApiEndpoint {
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("username") username:String,
        @Field("password") password:String
    ):Call<LoginResponse>

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name:String,
        @Field("username") username:String,
        @Field("password") password:String,
        @Field("android_id") android_id:String,
        @Field("divisi") divisi:String,
    ):Call<LoginResponse>

    @GET("absentData")
    fun listAttendance():Call<UserHistoryAbsentResponse>

    @GET("absentToday/{id}")
    fun getAbsentToday(
        @Path("id") id:String,
    ):Call<UserAbsentTodayResponse>

    @FormUrlEncoded
    @POST("absentMasuk")
    fun postAbsenMasuk(
        @Field("user_id") user_id:Int,
        @Field("android_id") android_id:String,
        @Field("jam_masuk") jam_masuk:String,
    ):Call<UserAbsentTodayResponse>

    @FormUrlEncoded
    @POST("absentPulang/{id}")
    fun postAbsenPulang(
        @Path("id") id:Int,
        @Field("jam_pulang") name:String,
    ):Call<UserAbsentTodayResponse>

    @GET("absentHistory/{user_id}")
    fun getHistoryAbsent(
        @Path("user_id") user_id: Int,
    ):Call<UserHistoryAbsentResponse>


    @GET("userData")
    fun userData(): Call<UserResponse>

    @POST("userDelete/{id}")
    fun userDelete(
        @Path("id") id:Int,
    ): Call<BaseResponse>

    @FormUrlEncoded
    @POST("userEdit/{id}")
    fun userEdit(
        @Path("id") id:Int,
        @Field("name") name:String,
        @Field("username") username:String,
        @Field("password") password:String,
        @Field("android_id") android_id:String,
        @Field("divisi") divisi:String,
    ):Call<LoginResponse>
}