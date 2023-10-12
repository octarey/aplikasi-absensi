package com.example.myabsensi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myabsensi.pojo.BaseResponse
import com.example.myabsensi.pojo.LoginResponse
import com.example.myabsensi.pojo.User
import com.example.myabsensi.pojo.UserResponse
import com.example.myabsensi.retrofit.ApiService
import com.example.myabsensi.utils.Helper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date
import kotlin.streams.asSequence

class UserActivity : AppCompatActivity() {
    private lateinit var listUser : RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var dateLabel: TextView
    private lateinit var btnAdd : TextView

    private var date : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        supportActionBar?.hide()

        dateLabel = findViewById(R.id.user_labelHeader)
        btnAdd = findViewById(R.id.user_menu)
        listUser = findViewById(R.id.listUser)
        userAdapter = UserAdapter(this)
        date = Helper.Utils.indonesianDate(Date())
        dateLabel.text = date

        listUser.apply {
            layoutManager = LinearLayoutManager(this@UserActivity, LinearLayoutManager.VERTICAL, false )
            adapter = userAdapter
            isNestedScrollingEnabled = true
        }

        getUser()

        btnAdd.setOnClickListener {
            val intent = Intent(this, FormUserActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getUser(){
        ApiService.endpoint.userData().enqueue(object : Callback<UserResponse>{
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                Log.d("pap list", response.body().toString())
                if (response.body()?.success == true){
                    try {
                        val data = response.body()?.data
                        if (data != null) {
                            userAdapter.setData(data)
                        }
                        action()
                    }catch (e:Exception){
                        Log.d("pap adapter",e.toString())
                    }

                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.d("pap gagal", t.toString())
                Toast.makeText(this@UserActivity, "Gagal load data", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun delete (id: Int){
        ApiService.endpoint.userDelete(id).enqueue(object : Callback<BaseResponse>{
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.isSuccessful){
                    Toast.makeText(this@UserActivity, "Hapus user berhasil", Toast.LENGTH_SHORT).show()
                    getUser()
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.d("pap gagal", t.toString())
                Toast.makeText(this@UserActivity, "Hapus user gagal", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun action(){
        userAdapter.setOnClickListener(object : UserAdapter.OnClickListener{
            override fun edit(position: Int, data: User) {
                val intent = Intent(this@UserActivity, FormUserActivity::class.java)
                intent.putExtra("data", data)
                startActivity(intent)
            }

            override fun delete(position: Int, data: User) {
                data.id?.let { delete(it) }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        getUser()
    }
}