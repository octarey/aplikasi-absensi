package com.example.myabsensi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myabsensi.pojo.LoginResponse
import com.example.myabsensi.pojo.UserAbsentTodayResponse
import com.example.myabsensi.retrofit.ApiService
import com.example.myabsensi.utils.Helper
import com.example.myabsensi.utils.ManagePermisions
import com.example.myabsensi.utils.PrefManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {

    private lateinit var inputUSer : EditText
    private lateinit var inputPassword : EditText
    private lateinit var titleLogin : TextView
    private lateinit var infoLogin : TextView
    private lateinit var btnLogin : Button
    private lateinit var btnLoginAdmin : TextView
    private lateinit var layoutLogin : CardView
    private lateinit var loadingLogin : ProgressBar
    private lateinit var prefManager: PrefManager
    private lateinit var view: View
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var managePermisions: ManagePermisions

    private var permissionRequestcode = 123
    private var type = 0  //0 untuk masuk , 1 untuk pulang
    private var userId = 0
    private var androidId = ""
    private var absenId = 0
    private var distance= 0
    private lateinit var office : Location
    private var page = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        init()
        if (cekRequestPermission()){
            checkAbsentToday(androidId)
        }

        btnLogin.setOnClickListener {
            val username = inputUSer.text.toString()
            val password = inputPassword.text.toString()
            if (username.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "username / password harus diisi", Toast.LENGTH_SHORT).show()
            }else {
                login(username, password)
            }
        }

        btnLoginAdmin.setOnClickListener {
            val intent = Intent(this@MainActivity, LoginAdminActivity::class.java)
            startActivity(intent)
        }
    }

    private fun init(){
        inputUSer = findViewById(R.id.login_dataUser)
        inputPassword = findViewById(R.id.login_dataPassword)
        titleLogin = findViewById(R.id.login_title)
        infoLogin = findViewById(R.id.login_location)
        btnLogin = findViewById(R.id.login_btn)
        btnLoginAdmin = findViewById(R.id.login_admin)
        layoutLogin = findViewById(R.id.login_box)
        loadingLogin = findViewById(R.id.login_loading)
        view = findViewById(R.id.login_layout)
        prefManager = PrefManager(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        if (intent.hasExtra("page")){
            page = intent.getStringExtra("page").toString()
        }

        office = Location("office")
        office.latitude = -7.949828
        office.longitude = 112.6206271

//        office.latitude = -8.194319
//        office.longitude = 112.7167632
    }

    private fun checkAbsentToday(androidId: String){
        ApiService.endpoint.getAbsentToday(androidId).enqueue(object : Callback<UserAbsentTodayResponse>{
            override fun onResponse(
                call: Call<UserAbsentTodayResponse>,
                response: Response<UserAbsentTodayResponse>
            ) {
                val data = response.body()?.data
                if (data == null){
                    loadingLogin.visibility = View.INVISIBLE
                    layoutLogin.visibility = View.VISIBLE
                    btnLoginAdmin.visibility = View.VISIBLE
                    prefManager.removeData()
                    titleLogin.text = getString(R.string.login, "MASUK")
                    btnLogin.text = getString(R.string.login, "MASUK")
                    type = 0
                }else {
                    absenId = data.id
                    checkLogin()
                }
            }

            override fun onFailure(call: Call<UserAbsentTodayResponse>, t: Throwable) {
                Helper.Utils.erroret("Server Error, Coba lagi nanti", R.color.error, view )
            }

        })
    }

    private fun checkLogin(){
        val login = prefManager.isLogin()
        val role = prefManager.getType()
        if (login == true && role.equals("user") && page == ""){
            val intent = Intent(this@MainActivity,HistoryActivity::class.java)
            intent.putExtra("userId", prefManager.getId())
            intent.putExtra("absenId", absenId)
            startActivity(intent)
            finish()
        }else{
            loadingLogin.visibility = View.INVISIBLE
            layoutLogin.visibility = View.VISIBLE
            btnLoginAdmin.visibility = View.VISIBLE
            titleLogin.text = getString(R.string.login, "PULANG")
            btnLogin.text = getString(R.string.login, "PULANG")
            type = 1
        }
    }

    private fun login(username: String, password: String){
        ApiService.endpoint.login(username,password).enqueue(object : Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val success = response.body()?.success
                val user = response.body()?.data?.name
                Log.d("pap login ",  success.toString())
                if (success == true) {
                    userId = response.body()?.data?.id!!
                    getLastKnownLocation()
                }else{
                    Toast.makeText(this@MainActivity, "Username /Password tidak sesuai", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.d("pap", t.message.toString())
                Toast.makeText(this@MainActivity, "Login gagal, coba beberapa saat lagi", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun absenMasuk(){
        val jamAbsen = Helper.Utils.getTime()
        ApiService.endpoint.postAbsenMasuk(userId, androidId, jamAbsen ).enqueue(object : Callback<UserAbsentTodayResponse>{
            override fun onResponse(
                call: Call<UserAbsentTodayResponse>,
                response: Response<UserAbsentTodayResponse>
            ) {
                if (response.isSuccessful){
                    Helper.Utils.erroret("Absen berhasil", R.color.success, view )
                    Handler().postDelayed({
                        val intent = Intent(this@MainActivity, HistoryActivity::class.java)
                        intent.putExtra("userId", userId)
                        intent.putExtra("absenId", absenId)
                        prefManager.setLoggin(true)
                        prefManager.setLogin("user",userId, absenId )
                        startActivity(intent)
                        finish()
                    }, 2000)
                }else{
                    Toast.makeText(this@MainActivity, "absen masuk gagal ${response.body()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserAbsentTodayResponse>, t: Throwable) {
                Helper.Utils.erroret("Server Error, Coba lagi nanti", R.color.error, view )
            }

        })
    }

    private fun absenPulang(){
        val jamAbsen = Helper.Utils.getTime()
        ApiService.endpoint.postAbsenPulang(absenId, jamAbsen ).enqueue(object : Callback<UserAbsentTodayResponse>{
            override fun onResponse(
                call: Call<UserAbsentTodayResponse>,
                response: Response<UserAbsentTodayResponse>
            ) {
                if (response.isSuccessful){
                    Helper.Utils.erroret("Absen berhasil", R.color.success, view )
                    Handler().postDelayed({
                        val intent = Intent(this@MainActivity, HistoryActivity::class.java)
                        intent.putExtra("userId", userId)
                        intent.putExtra("absenId", absenId)
                        prefManager.setLoggin(true)
                        prefManager.setLogin("user",userId, absenId )
                        startActivity(intent)
                        finish()
                    }, 2000)
                }else{
                    Toast.makeText(this@MainActivity, "absen pulang gagal", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserAbsentTodayResponse>, t: Throwable) {
                Helper.Utils.erroret("Server Error, Coba lagi nanti", R.color.error, view )
            }

        })
    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
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
                        infoLogin.visibility = View.VISIBLE
                        infoLogin.text = getString(R.string.info_lokasi, "luar", "datang ke tempat kerja")
                        Helper.Utils.erroret("Absen gagal", R.color.error, view )
                    }else{
                        if (type == 0) absenMasuk() else absenPulang()
                    }
                }else{
                    Helper.Utils.erroret("Gagal mendapatkan lokasi", R.color.error, view )
                }

            }
    }

    fun cekRequestPermission(): Boolean {
        val coarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        val fineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

        val listPermission = ArrayList<String>()
        if (coarseLocation != PackageManager.PERMISSION_GRANTED) {
            listPermission.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (fineLocation != PackageManager.PERMISSION_GRANTED) {
            listPermission.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (!listPermission.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermission.toTypedArray(), permissionRequestcode)
            return false
        }

        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permissionRequestcode -> {
                val perms = HashMap<String, Int>()
                perms[Manifest.permission.ACCESS_COARSE_LOCATION] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.ACCESS_FINE_LOCATION] = PackageManager.PERMISSION_GRANTED

                if (grantResults.size > 0) {
                    for (i in permissions.indices)
                        perms[permissions[i]] = grantResults[i]

                    if (perms[Manifest.permission.ACCESS_COARSE_LOCATION] == PackageManager.PERMISSION_GRANTED &&
                        perms[Manifest.permission.ACCESS_FINE_LOCATION] == PackageManager.PERMISSION_GRANTED) {
                        checkAbsentToday(androidId)
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            val bld = AlertDialog.Builder(this)
                                .setMessage("Service Permissions are required for this app")
                                .setPositiveButton("OK") { dialog, wich ->
                                    cekRequestPermission()
                                }
                                .setNegativeButton("Cancel") { dialog, wich ->
                                    finish()
                                };
                            val dialog = bld.create()
                            dialog.show()
                        }
                    }
                }
            }
        }
    }
}