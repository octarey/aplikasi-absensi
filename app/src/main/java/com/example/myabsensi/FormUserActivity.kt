package com.example.myabsensi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.myabsensi.pojo.LoginResponse
import com.example.myabsensi.pojo.User
import com.example.myabsensi.retrofit.ApiService
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.streams.asSequence

class FormUserActivity : AppCompatActivity() {
    private lateinit var nameInput : EditText
    private lateinit var usernameInput : EditText
    private lateinit var passwordInput : EditText
    private lateinit var divisiInput : EditText
    private lateinit var simpanBtn : Button
    private lateinit var cancelBtn : Button
    private val divisi = listOf("Ketatausahaan", "Kehumasan", "Kepegawaian", "Teknik")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_user)
        supportActionBar?.hide()


        nameInput = findViewById(R.id.detailNama)
        usernameInput = findViewById(R.id.detailUsername)
        passwordInput = findViewById(R.id.detailPassword)
        divisiInput = findViewById(R.id.detailDivisi)
        simpanBtn = findViewById(R.id.detailSimpan)
        cancelBtn = findViewById(R.id.detailCancel)

        val data = intent.getSerializableExtra("data") as? User

        if (data != null){
            nameInput.setText(data.name)
            usernameInput.setText(data.username)
           // passwordInput.setText(data.password)
        }

        simpanBtn.setOnClickListener {
            val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

            val androidId = java.util.Random().ints(10, 0, source.length)
                .asSequence()
                .map(source::get)
                .joinToString("")
            val name = nameInput.text.toString()
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            val divisi = divisiInput.text.toString()

            if (data != null){
                data.id?.let { it1 -> updateUser(it1, name, username, password, divisi, androidId) }
            }else{
                addUser(name, username, password, divisi, androidId)
            }
        }
        cancelBtn.setOnClickListener { onBackPressed() }

        val adapter = ArrayAdapter(this, R.layout.item_dropdown, divisi)
        val layoutDivisi = findViewById<TextInputLayout>(R.id.layoutDivisi)
        (layoutDivisi.editText as? AutoCompleteTextView)?.setAdapter(adapter)

    }

    private fun addUser(name: String, username: String, password: String, divisi: String, androidId: String){
        ApiService.endpoint.register(name, username, password, androidId, divisi).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                Toast.makeText(this@FormUserActivity, "Tambah user berhasil", Toast.LENGTH_SHORT).show()
                onBackPressed()
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@FormUserActivity, "Tambah user gagal", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun updateUser(id: Int, name: String, username: String, password: String, divisi: String, androidId: String){
       ApiService.endpoint.userEdit(id, name, username, password, androidId, divisi).enqueue(object :
            Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                Toast.makeText(this@FormUserActivity, "Update user berhasil", Toast.LENGTH_SHORT).show()
                onBackPressed()
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@FormUserActivity, "Update user gagal", Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}