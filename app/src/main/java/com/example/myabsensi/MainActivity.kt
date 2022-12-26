package com.example.myabsensi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.myabsensi.pojo.LoginResponse
import com.example.myabsensi.retrofit.ApiService
import com.example.myabsensi.utils.PrefManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var inputUSer : EditText
    private lateinit var inputPassword : EditText
    private lateinit var btnLogin : Button
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        init()
        checkLogin()

        btnLogin.setOnClickListener(this)
    }

    private fun init(){
        inputUSer = findViewById(R.id.login_dataUser)
        inputPassword = findViewById(R.id.login_dataPassword)
        btnLogin = findViewById(R.id.login_btn)
        prefManager = PrefManager(this)
    }

    private fun checkLogin(){
        val login = prefManager.isLogin()
        val type = prefManager.getType()
        if (login == true && type.equals("user")){
            val intent = Intent(this@MainActivity,UserActivity::class.java)
            startActivity(intent)
            finish()
        }else if (login == true && type.equals("admin")){
            val intent = Intent(this@MainActivity,AdminActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun login(){
        val username = inputUSer.text.toString()
        val password = inputPassword.text.toString()
        ApiService.endpoint.login(username,password).enqueue(object : Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val success = response.body()?.success
                val user = response.body()?.data?.name
                val token = response.body()?.data?.token
                val id = response.body()?.data?.id
                Log.d("pap login ",  success.toString())
                if (success == true && user.equals("admin")){
                    val intent = Intent(this@MainActivity,AdminActivity::class.java)
                    prefManager.setLoggin(true)
                    prefManager.setLogin("user",token,user,id!!)
                    startActivity(intent)
                    finish()
                }else if (success == true) {
                    val intent = Intent(this@MainActivity,UserActivity::class.java)
                    prefManager.setLoggin(true)
                    prefManager.setLogin("user",token,user,id!!)
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(this@MainActivity, "Username /Password tidak sesuai", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.d("pap", t.message.toString())
                Toast.makeText(this@MainActivity, "Login gagal, coba beberapa saat lagi", Toast.LENGTH_SHORT).show()
            }

        })
//        if (username.equals("jhon") && password.equals("user")){
//            val intent = Intent(this,UserActivity::class.java)
//            intent.putExtra("distance", distance)
//            intent.putExtra("username", username)
//            startActivity(intent)
//        } else if (username.equals("admin") && password.equals("admin")){
//            val intent = Intent(this,AdminActivity::class.java)
//            startActivity(intent)
//        } else {
//            Toast.makeText(this, "Login Gagal", Toast.LENGTH_SHORT).show()
//        }

    }

    override fun onClick(p0: View) {
        when(p0.id){
            R.id.login_btn -> {
                login()
            }
        }
    }
}