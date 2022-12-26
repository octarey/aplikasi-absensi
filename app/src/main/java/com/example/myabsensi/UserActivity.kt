package com.example.myabsensi

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import coil.api.load
import com.example.myabsensi.pojo.*
import com.example.myabsensi.retrofit.ApiService
import com.example.myabsensi.utils.PrefManager
import com.google.android.gms.common.api.Api
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UserActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var headerName : TextView
    private lateinit var headerDivision : TextView
    private lateinit var absenDateTv : TextView
    private lateinit var jamMasukTv : TextView
    private lateinit var lokasiMasukTv : TextView
    private lateinit var feedbackMasukTv : TextView
    private lateinit var jamPulangTv : TextView
    private lateinit var lokasiPulangTv : TextView
    private lateinit var feedbackPulangTv :TextView
    private lateinit var imageMasuk : ImageView
    private lateinit var imagePulang : ImageView
    private lateinit var btnAbsen : Button

    private lateinit var reportAll : TextView
    private lateinit var reportOn : TextView
    private lateinit var reportLate : TextView

    private lateinit var userImage : ImageView
    private lateinit var userCoordinate : TextView
    private lateinit var userTime : TextView
    private lateinit var btnImage : ImageButton
    private lateinit var btnSubmit : Button
    private lateinit var btnLogout : ImageButton
    private lateinit var prefManager: PrefManager

    private var user_id = 0
    private var absen_id = 0
    private var division = ""
    private var username = ""
    private var tokenUser = ""
    private var absensi : Absensi? = null
    private var listAbsents : List<Absensi>? = null

    private val CAMERA_REQUEST = 1888
    private val MY_CAMERA_PERMISSION_CODE = 100

    private var distance= 0
    lateinit var office : Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        supportActionBar?.hide()

        init()
        getOfficeLocation()
        getUser()
        getHistoryAbsent()
        getAbsentToday()

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {}
                    BottomSheetBehavior.STATE_EXPANDED -> {}
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        userTime.text = ""
                        userCoordinate.text = ""
                        userImage.setImageBitmap(null)
                        btnImage.visibility = View.VISIBLE
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {}
                    BottomSheetBehavior.STATE_SETTLING -> {}
                    else -> {}
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

        })

        absenDateTv.text = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id")).format(Date()).toString()

        btnAbsen.setOnClickListener(this)
        btnImage.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)
        btnLogout.setOnClickListener(this)
    }

    private fun refresh(){
        getUser()
        getHistoryAbsent()
        getAbsentToday()
    }
    private fun init(){
        headerName = findViewById(R.id.user_headerName)
        headerDivision = findViewById(R.id.user_headerDiv)
        absenDateTv = findViewById(R.id.user_absentDate)
        jamMasukTv = findViewById(R.id.user_masukTime)
        lokasiMasukTv = findViewById(R.id.user_masukLoc)
        imageMasuk = findViewById(R.id.user_masukImg)
        jamPulangTv = findViewById(R.id.user_pulangTime)
        lokasiPulangTv = findViewById(R.id.user_pulangLoc)
        imagePulang = findViewById(R.id.user_pulangImg)
        feedbackMasukTv = findViewById(R.id.user_masukFeedback)
        feedbackPulangTv = findViewById(R.id.user_pulangFeedback)
        btnAbsen = findViewById(R.id.user_btnAbsen)

        reportAll = findViewById(R.id.user_reportAll)
        reportOn = findViewById(R.id.user_reportOn)
        reportLate = findViewById(R.id.user_reportLate)

        userImage = findViewById(R.id.user_dataImage)
        userCoordinate = findViewById(R.id.user_tvLocation)
        userTime = findViewById(R.id.user_tvTime)

        btnImage = findViewById(R.id.user_btnPhoto)
        btnSubmit = findViewById(R.id.user_btnSubmit)
        btnLogout = findViewById(R.id.user_logoutBtn)

        prefManager = PrefManager(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.user_absentBox))

        tokenUser = prefManager.getToken().toString()
        user_id = prefManager.getId()
    }

    private fun getOfficeLocation(){
        //set lokasi absensi(kantor)
        office = Location("office")
        //office.latitude = -7.9723572
        //office.longitude = 112.6292202

        //lokasi kos
        office.latitude = -7.8665146
        office.longitude = 112.6802038
    }

    private fun updateUI() {
        headerName.text = username.uppercase()
        headerDivision.text = "STAFF " + division
        if (!listAbsents.isNullOrEmpty()){
            reportAll.text = listAbsents?.size.toString()
            reportOn.text = listAbsents?.count { it.status_masuk == "ONTIME" }.toString()
            reportLate.text = listAbsents?.count { it.status_masuk == "TERLAMBAT" }.toString()
        }



        if (absensi != null) {
            absen_id = absensi!!.id
            jamMasukTv.text = "${absensi?.jam_masuk} WIB"
            lokasiMasukTv.text = "${absensi?.lokasi_masuk} Km dari kantor"
            imageMasuk.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.user_male))
            if (absensi!!.status_masuk == "TERLAMBAT"){
                feedbackMasukTv.text = "KAMU TERLAMBAT, DATANG LEBIH PAGI YA"
            }else{
                feedbackMasukTv.text = "TERIMA KASIH, KAMU DATANG TEPAT WAKTU"
            }

            if (absensi?.jam_pulang != null) {
                jamPulangTv.text = "${absensi?.jam_pulang} WIB"
                lokasiPulangTv.text = "${absensi?.lokasi_pulang} Km dari kantor"
                imagePulang.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.user_male))
                btnAbsen.visibility = View.GONE

                if (absensi!!.status_pulang == "TERLAMBAT"){
                    feedbackPulangTv.text = "KAMU PULANG LEBIH CEPAT, IZIN DULU YA"
                }else{
                    feedbackPulangTv.text = "TERIMA KASIH UNTUK HARI INI"
                }
            }else{
                btnAbsen.text = "ABSEN PULANG"
                btnSubmit.text = "ABSEN PULANG"
            }
        } else {
            btnAbsen.text = "ABSEN MASUK"
            btnSubmit.text = "ABSEN MASUK"
        }
    }

    private fun getUser(){
        val auth = "Bearer $tokenUser"
        ApiService.endpoint.getUser(user_id, auth).enqueue(object : Callback<UserDetailResponse>{
            override fun onResponse(
                call: Call<UserDetailResponse>,
                response: Response<UserDetailResponse>
            ) {
                println("pap success get user ${response.body()}")
                username = response.body()?.data?.name ?: ""
                division = response.body()?.data?.division ?: ""
                updateUI()
            }

            override fun onFailure(call: Call<UserDetailResponse>, t: Throwable) {
                println("pap gagal get user ${t.toString()}")
                refresh()
            }

        })
    }

    private fun getAbsentToday(){
        val auth = "Bearer $tokenUser"
        ApiService.endpoint.getAbsentToday(user_id, auth).enqueue(object : Callback<UserAbsentTodayResponse>{
            override fun onResponse(
                call: Call<UserAbsentTodayResponse>,
                response: Response<UserAbsentTodayResponse>
            ) {
                if (response.isSuccessful){
                    val data = response.body()
                    absensi = data?.data
                    updateUI()
                }else{
                    println("pap ${response.body()}")
                    Toast.makeText(this@UserActivity, "Gagal Mendapat Data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserAbsentTodayResponse>, t: Throwable) {
                println("pap gagal get absent ${t.toString()}")
            }

        })
    }

    private fun getHistoryAbsent(){
        val auth = "Bearer $tokenUser"
        ApiService.endpoint.getHistoryAbsent(user_id, auth).enqueue(object :Callback<UserHistoryAbsentResponse>{
            override fun onResponse(
                call: Call<UserHistoryAbsentResponse>,
                response: Response<UserHistoryAbsentResponse>
            ) {
                listAbsents = response.body()!!.data
                updateUI()
            }

            override fun onFailure(call: Call<UserHistoryAbsentResponse>, t: Throwable) {
                println("pap gagal get history absent ${t.toString()}")
            }

        })
    }

    private fun userPhoto(){
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST)
    }

    private fun sendData(action: String){
        val auth = "Bearer $tokenUser"
        if (action.equals("masuk")){
            ApiService.endpoint.postAbsenMasuk(user_id,distance,auth).enqueue(object : Callback<UserAbsentTodayResponse>{
                override fun onResponse(
                    call: Call<UserAbsentTodayResponse>,
                    response: Response<UserAbsentTodayResponse>
                ) {
                    val success = response.body()?.success
                    Log.d("pap absen masuk",response.body().toString())
                    if (success == true){
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        erroret("Absensi Masuk Berhasil", R.color.success)
                        getHistoryAbsent()
                        getAbsentToday()
                    }else{
                        erroret("Absensi Masuk Gagal", R.color.warning)
                    }
                }

                override fun onFailure(call: Call<UserAbsentTodayResponse>, t: Throwable) {
                    Log.d("pap gagal absen",t.toString())
                    erroret("Gagal memproses, coba lagi nanti", R.color.error)
                }

            })
        }else{
            ApiService.endpoint.postAbsenPulang(absen_id,distance,auth).enqueue(object : Callback<UserAbsentTodayResponse>{
                override fun onResponse(
                    call: Call<UserAbsentTodayResponse>,
                    response: Response<UserAbsentTodayResponse>
                ) {
                    val success = response.body()?.success
                    Log.d("pap absen pulang",response.body().toString())
                    if (success == true){
                        erroret("Absensi Pulang Berhasil", R.color.success)
                        getHistoryAbsent()
                        getAbsentToday()
                    }else{
                        erroret("Absensi Pulang Gagal", R.color.warning)
                    }
                }

                override fun onFailure(call: Call<UserAbsentTodayResponse>, t: Throwable) {
                    Log.d("pap gagal absen",t.toString())
                    erroret("Gagal memproses, coba lagi nanti", R.color.error)
                }

            })

        }
    }

    private fun showInfo(){
        //info radius
        if (distance <= 1 ){
            userCoordinate.text = "(0 Km) Berada di Kantor"
        }else{
            userCoordinate.text = "$distance Km dari Kantor"
            userCoordinate.setTextColor(Color.RED)
        }

        //info waktu
        val sdf = SimpleDateFormat("HH:mm:ss")
        val date = sdf.format(Date())
        userTime.text = "$date WIB"
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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location->
                if (location != null) {
                    // use your location object
                    // get latitude , longitude and other info from this
                    distance = location.distanceTo(office).toInt()/1000
                    Log.d("pap", "${location.latitude} ${location.longitude}")
                }

            }

    }

    private fun erroret(msg: String, color:Int){
        val snackbar = Snackbar.make(findViewById(R.id.user_layout), msg, Snackbar.LENGTH_LONG)
        snackbar.view.minimumHeight = 20
        snackbar.view.setBackgroundResource(color)
        snackbar.setTextColor(Color.BLACK)
        snackbar.show()
    }

    override fun onClick(p0: View) {
        when(p0.id){
            R.id.user_btnPhoto -> {
                getLastKnownLocation()
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.CAMERA), MY_CAMERA_PERMISSION_CODE
                    )
                } else {
                    userPhoto()
                }
            }
            R.id.user_btnSubmit -> {
                if (distance != 0){
                    erroret("Harap datang ke kantor untuk melakukan absensi", R.color.warning)
                }else {
                    if (btnSubmit.text == "ABSEN MASUK"){
                        sendData("masuk")
                    }else {
                        sendData("pulang")
                    }

                }
            }
            R.id.user_btnAbsen -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
            R.id.user_logoutBtn -> {
                prefManager.removeData()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_CAMERA_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0]
                            == PackageManager.PERMISSION_GRANTED)
                ) {
                    userPhoto()
                } else {
                    Toast.makeText(
                        this,
                        "Izinkan aplikasi mengakses kamera",
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }
            else -> {
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == CAMERA_REQUEST) {
                val photo = data!!.extras!!.get("data") as Bitmap?
                photo?.let {
                    btnImage.visibility = View.GONE
                    showInfo()
                    userImage.setImageBitmap(it)
                }
            }
        }
    }

}