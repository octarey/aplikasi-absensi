package com.example.myabsensi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myabsensi.pojo.UserAbsentTodayResponse
import com.example.myabsensi.pojo.UserHistoryAbsentResponse
import com.example.myabsensi.retrofit.ApiService
import com.example.myabsensi.utils.Helper
import com.example.myabsensi.utils.PrefManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RequiresApi(Build.VERSION_CODES.O)
class HistoryActivity : AppCompatActivity() {
    private lateinit var historyList_rv : RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var historyInfo: TextView
    private lateinit var prefManager: PrefManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var distance= 0
    private var absenId = 0
    private lateinit var office : Location
    private val handler = Handler()

    private var user_id = 0
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        supportActionBar?.hide()

        init()

        getHistoryAbsent()

        handler.postDelayed(runnable, 3000)

        val backBtn = findViewById<ImageButton>(R.id.logoutBtn)

        backBtn.setOnClickListener {
            absenPulang("pulang")
        }

    }

    private fun init(){
        prefManager = PrefManager(this)
        historyList_rv = findViewById(R.id.fragment_historyList)
        historyInfo = findViewById(R.id.historyEmpty)
        user_id = intent.getIntExtra("userId", 0)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        office = Location("office")
        office.latitude = -7.949828
        office.longitude = 112.6206271

        if (intent.hasExtra("absenId")){
            absenId = intent.getIntExtra("absenId", 0)
        }

    }

    private fun getHistoryAbsent(){
        ApiService.endpoint.getHistoryAbsent(user_id).enqueue(object :
            Callback<UserHistoryAbsentResponse> {
            override fun onResponse(
                call: Call<UserHistoryAbsentResponse>,
                response: Response<UserHistoryAbsentResponse>
            ) {
                val data = response.body()?.data

                if (data != null){
                    historyAdapter = HistoryAdapter(this@HistoryActivity, data )
                    historyList_rv.layoutManager = LinearLayoutManager(
                        this@HistoryActivity,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                    historyList_rv.adapter = historyAdapter
                }else{
                    historyInfo.visibility = View.VISIBLE
                }

            }

            override fun onFailure(call: Call<UserHistoryAbsentResponse>, t: Throwable) {
                println("pap gagal get history absent ${t.toString()}")
            }

        })
    }

    private fun absenPulang(type : String){
        val jamAbsen = Helper.Utils.getTime()
        ApiService.endpoint.postAbsenPulang(absenId, jamAbsen ).enqueue(object : Callback<UserAbsentTodayResponse>{
            override fun onResponse(
                call: Call<UserAbsentTodayResponse>,
                response: Response<UserAbsentTodayResponse>
            ) {
                if (response.isSuccessful){
                    if (type.equals("pulang")){
                        Helper.Utils.erroret("Absensi pulang berhasil", R.color.success, historyInfo )
                    }else {
                        Helper.Utils.erroret("Anda terdeteksi keluar dari area kerja", R.color.error, historyInfo )
                    }
                    handler.postDelayed({
                        logout()
                    },3000)

                }else{
                    Helper.Utils.erroret("Absen pulang gagal", R.color.error, historyInfo )
                }
            }

            override fun onFailure(call: Call<UserAbsentTodayResponse>, t: Throwable) {
                Helper.Utils.erroret("Server Error, Coba lagi nanti", R.color.error, historyInfo )
            }

        })
    }

    var runnable: Runnable = object : Runnable {
        override fun run() {
            if (ActivityCompat.checkSelfPermission(
                    this@HistoryActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this@HistoryActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location->
                    if (location != null) {
                        // use your location object
                        // get latitude , longitude and other info from this
                        distance = location.distanceTo(office).toInt()
                        if (distance > 50000){
//                            Toast.makeText(this@HistoryActivity, "${location.latitude} ${location.longitude} $distance", Toast.LENGTH_SHORT).show()
                            absenPulang("detect")
                        }
                    }

                }
            handler.postDelayed(this, 150000)
        }
    }

    private fun logout(){
        prefManager.removeData()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }
}