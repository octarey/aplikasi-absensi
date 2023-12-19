package com.example.myabsensi

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myabsensi.pojo.Absensi
import com.example.myabsensi.pojo.UserHistoryAbsentResponse
import com.example.myabsensi.retrofit.ApiService
import com.example.myabsensi.utils.Helper
import com.example.myabsensi.utils.PrefManager
import com.example.myabsensi.utils.SessionManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class AdminActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var dateLabel : TextView
    private lateinit var emptyLabel : TextView
    private lateinit var countOk : TextView
    private lateinit var countNok : TextView
    private lateinit var listAbsen : RecyclerView
    private lateinit var btnFilter : ImageButton
    private lateinit var keyFilter : EditText
    private lateinit var attendanceAdapter: AttendanceAdapter
    private lateinit var btnPrint : FloatingActionButton
    private lateinit var btnLocation : FloatingActionButton
    private lateinit var btnUser : TextView
    private lateinit var dataAbsen : List<Absensi>
    private lateinit var sessionManager: SessionManager

    private var date : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        supportActionBar?.hide()

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            date = SimpleDateFormat("EEEE, dd MMMM yyyy",Locale("id")).format(Date()).toString()
//        }

        date = Helper.Utils.indonesianDate(Date())
        sessionManager = SessionManager(this)

        val type = intent.getStringExtra("type")

        dateLabel = findViewById(R.id.admin_dateHeader)
        emptyLabel = findViewById(R.id.adminEmpty)
        countOk = findViewById(R.id.admin_footerOK)
        countNok = findViewById(R.id.admin_footerNOK)
        btnFilter = findViewById(R.id.filterBtn)
        keyFilter = findViewById(R.id.filterKeyword)
        listAbsen = findViewById(R.id.admin_rvAbsen)
        btnPrint = findViewById(R.id.admin_btnPrint)
        btnLocation = findViewById(R.id.admin_btnLocation)
        btnUser = findViewById(R.id.admin_menu)
        val btnLogout = findViewById<TextView>(R.id.admin_logout)

        dateLabel.text = "data diperbaharui : $date"

        if (type != null) {
            setMenu(type)
        }
        getAttendance()

        btnFilter.setOnClickListener(this)
        btnPrint.setOnClickListener(this)
        btnLocation.setOnClickListener(this)
        btnUser.setOnClickListener(this)
        btnLogout.setOnClickListener(this)

    }

    private fun setMenu(type : String){
        if (type == "admin"){
            btnUser.visibility = View.VISIBLE
            btnPrint.visibility = View.VISIBLE
        }else{
            btnUser.visibility = View.GONE
            btnPrint.visibility = View.GONE
        }
    }

    private fun getAttendance(){
        ApiService.endpoint.listAttendance().enqueue(object : Callback<UserHistoryAbsentResponse>{
            override fun onResponse(
                call: Call<UserHistoryAbsentResponse>,
                response: Response<UserHistoryAbsentResponse>
            ) {
                Log.d("pap list", response.body().toString())
                if (response.body()?.success == true){
                    try {
                        val data = response.body()?.data?.sortedByDescending { it.id }
                        if (data != null) {
                            dataAbsen = data
                        }

                        if (dataAbsen.isEmpty()){
                            emptyLabel.visibility = View.VISIBLE
                        }

                        attendanceAdapter = AttendanceAdapter(this@AdminActivity, dataAbsen)
                        listAbsen.layoutManager = LinearLayoutManager(
                            this@AdminActivity,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                        listAbsen.adapter = attendanceAdapter

                        val ok = data?.count() { it.status_masuk == "ontime" && it.status_pulang == "ontime" }
                        val nok = data?.count() { it.status_masuk == "late" || it.status_pulang == "late" }
                        countOk.text = "Sesuai jadwal $ok Karyawan"
                        countNok.text = "Tidak sesuai jadwal $nok Karyawan"
                    }catch (e:Exception){
                        Log.d("pap adapter",e.toString())
                    }

                }
            }

            override fun onFailure(call: Call<UserHistoryAbsentResponse>, t: Throwable) {
                Log.d("pap gagal", t.toString())
                Toast.makeText(this@AdminActivity, "Gagal load data", Toast.LENGTH_SHORT).show()
            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(p0: View) {
        when(p0.id) {
            R.id.admin_menu -> {
                val intent = Intent(this@AdminActivity, UserActivity::class.java)
                startActivity(intent)
            }

            R.id.admin_logout -> {
                val intent = Intent(this@AdminActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            R.id.filterBtn -> {
                val key = keyFilter.text.toString()
                val result = dataAbsen.filter { it.user.name.lowercase().contains(key) }
                attendanceAdapter.update(result)

                if (result.isEmpty()){
                    emptyLabel.visibility = View.VISIBLE
                    emptyLabel.text = "Data tidak ditemukan"
                }else {
                    emptyLabel.visibility = View.GONE
                }
            }

            R.id.admin_btnLocation -> {
                val dialog = Dialog(this)
                dialog.window?.requestFeature(Window.FEATURE_NO_TITLE) // if you have blue line on top of your dialog, you need use this code
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.setContentView(R.layout.layout_edit_location)
                val inputLatitude = dialog.findViewById(R.id.inputLatitude) as EditText
                val inputLongitude = dialog.findViewById(R.id.inputLongitude) as EditText
                val inputRadius = dialog.findViewById(R.id.inputRadius) as EditText
                val dialogPositiveButton = dialog.findViewById(R.id.btnSave) as Button
                val dialogNegativeButton = dialog.findViewById(R.id.btnCancel) as Button
                dialogPositiveButton.setOnClickListener {
                    val lat = inputLatitude.text.toString().replace("\\s".toRegex(), "")
                    val long = inputLongitude.text.toString().replace("\\s".toRegex(), "")
                    val radius = inputRadius.text.toString()
                    sessionManager.save("latitude", lat)
                    sessionManager.save("longitude", long)
                    sessionManager.save("radius", radius)
                    Helper.Utils.erroret("Lokasi absensi berhasil diubah", R.color.success, btnLocation)
                    dialog.dismiss()
                }
                dialogNegativeButton.setOnClickListener {
                    inputLatitude.setText("")
                    inputLongitude.setText("")
                    inputRadius.setText("")
                    dialog.dismiss()
                }

                val lp = WindowManager.LayoutParams()
                lp.copyFrom(dialog.window!!.attributes)
                lp.width = WindowManager.LayoutParams.MATCH_PARENT
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT
                dialog.show()
                val window = dialog.window
                window!!.attributes = lp
            }

            R.id.admin_btnPrint -> {
                Toast.makeText(this, "Export data absensi, harap tunggu...", Toast.LENGTH_SHORT).show()
                val hssfWorkbook = HSSFWorkbook()
                val hssfSheet = hssfWorkbook.createSheet("Custom Sheet")
                //---------------
                //---------------
                val hssfRowMainTitle = hssfSheet.createRow(0)
                hssfRowMainTitle.createCell(0).setCellValue("Data absensi diperbarui $date")
                //-------------
                //-------------
                val hssfRowTitle = hssfSheet.createRow(1)
                hssfRowTitle.createCell(0).setCellValue("no")
                hssfRowTitle.createCell(1).setCellValue("tanggal")
                hssfRowTitle.createCell(2).setCellValue("nama")
                hssfRowTitle.createCell(3).setCellValue("divisi")
                hssfRowTitle.createCell(4).setCellValue("jam datang")
                hssfRowTitle.createCell(5).setCellValue("jam pulang")
                hssfRowTitle.createCell(6).setCellValue("status datang")
                hssfRowTitle.createCell(7).setCellValue("status pulang")
                //--------------
                //--------------
                var row = 2
                var srNo = 1
                for (a in dataAbsen) {
                    val hssfRow = hssfSheet.createRow(row)
                    val status = if (a.status_masuk.equals("late")) "Terlambat" else "Ok"
                    var statusPulang = ""
                    if (a.status_pulang.isNullOrEmpty()){
                        statusPulang = "Tidak Absen"
                    }else {
                        statusPulang = if (a.status_pulang.equals("late")) "Pulang Awal" else "Ok"
                    }

                    val date = Helper.Utils.indonesianDate(a.created_at)
                    hssfRow.createCell(0).setCellValue(srNo.toDouble())
                    hssfRow.createCell(1).setCellValue(date)
                    hssfRow.createCell(2).setCellValue(a.user.name)
                    hssfRow.createCell(3).setCellValue(a.user.divisi)
                    hssfRow.createCell(4).setCellValue(a.jam_masuk)
                    hssfRow.createCell(5).setCellValue(a.jam_pulang)
                    hssfRow.createCell(6).setCellValue(status)
                    hssfRow.createCell(7).setCellValue(statusPulang)
                    row++
                    srNo++
                }
                //---------
                //---------
                val path: File =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + File.separator)
                val file = File(path.toString())
                file.mkdirs()
                val fileName: String =
                    path.toString() + "/" + "absensi" + System.currentTimeMillis() + ".xls"
                try {
                    val fileOutputStream = FileOutputStream(fileName)
                    hssfWorkbook.write(fileOutputStream)
                    fileOutputStream.flush()
                    fileOutputStream.close()
                    Toast.makeText(this, "Export successfully", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    Toast.makeText(this, "Export failed", Toast.LENGTH_SHORT).show()
                    Log.d("pap gagal print", e.toString())
                    e.printStackTrace()
                }
            }
        }
    }
}