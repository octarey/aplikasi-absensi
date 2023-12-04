package com.example.myabsensi

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
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
    lateinit var dateLabel : TextView
    lateinit var emptyLabel : TextView
    lateinit var countOk : TextView
    lateinit var countNok : TextView
    lateinit var listAbsen : RecyclerView
    lateinit var attendanceAdapter: AttendanceAdapter
    lateinit var btnPrint : FloatingActionButton
    lateinit var dataAbsen : List<Absensi>

    private var date : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        supportActionBar?.hide()

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            date = SimpleDateFormat("EEEE, dd MMMM yyyy",Locale("id")).format(Date()).toString()
//        }

        date = Helper.Utils.indonesianDate(Date())

        dateLabel = findViewById(R.id.admin_dateHeader)
        emptyLabel = findViewById(R.id.adminEmpty)
        countOk = findViewById(R.id.admin_footerOK)
        countNok = findViewById(R.id.admin_footerNOK)
        listAbsen = findViewById(R.id.admin_rvAbsen)
        btnPrint = findViewById(R.id.admin_btnPrint)
        val btnUser = findViewById<TextView>(R.id.admin_menu)
        val btnLogout = findViewById<TextView>(R.id.admin_logout)

        dateLabel.text = "data diperbaharui : $date"

        getAttendance()

        btnPrint.setOnClickListener(this)
        btnUser.setOnClickListener(this)
        btnLogout.setOnClickListener(this)

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
                        val data = response.body()?.data
                        dataAbsen = response.body()?.data!!
                        if (dataAbsen.isEmpty()){
                            emptyLabel.visibility = View.VISIBLE
                        }

                        attendanceAdapter = AttendanceAdapter(this@AdminActivity, data)
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
                    val status = if (a.status_masuk.equals("OK")) "On Time" else "Terlambat"
                    var statusPulang = ""
                    if (a.status_pulang.isNullOrEmpty()){
                        statusPulang = "Tidak Absen"
                    }else {
                        statusPulang = if (a.status_pulang.equals("OK")) "On Time" else "Pulang Awal"
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