package com.example.myabsensi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginAdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_admin)
        supportActionBar?.hide()

        val btnLogin = findViewById<Button>(R.id.adminLogin_btn)
        val inputUsername = findViewById<EditText>(R.id.adminLogin_dataUser)
        val inputPassword = findViewById<EditText>(R.id.adminLogin_dataPassword)
        
        btnLogin.setOnClickListener {
            val username = inputUsername.text.toString()
            val password = inputPassword.text.toString()
            
            if (username.equals("admin") && password.equals("admin")){
                val intent = Intent(this@LoginAdminActivity,  AdminActivity::class.java)
//            prefManager.setLoggin(true)
//            prefManager.setLogin("admin",userId )
//            intent.putExtra("userId", userId)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this, "Login gagal, username / password tidak sesuai", Toast.LENGTH_SHORT).show()
            }
           
        }

    }
}