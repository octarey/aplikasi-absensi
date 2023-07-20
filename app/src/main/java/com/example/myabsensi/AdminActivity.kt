package com.example.myabsensi

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myabsensi.pojo.Attendance
import com.example.myabsensi.pojo.AttendanceListResponse
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
import java.text.SimpleDateFormat
import java.util.*


class AdminActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var dateLabel : TextView
    lateinit var countOk : TextView
    lateinit var countNok : TextView
    lateinit var listAbsen : RecyclerView
    lateinit var attendanceAdapter: AttendanceAdapter
    lateinit var btnPrint : FloatingActionButton
    lateinit var dataAbsen : List<Attendance>

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
        countOk = findViewById(R.id.admin_footerOK)
        countNok = findViewById(R.id.admin_footerNOK)
        listAbsen = findViewById(R.id.admin_rvAbsen)
        btnPrint = findViewById(R.id.admin_btnPrint)

        dateLabel.text = date

        getAttendance()

        btnPrint.setOnClickListener(this)
    }


    private fun getAttendance(){
        ApiService.endpoint.listAttendance().enqueue(object : Callback<AttendanceListResponse>{
            override fun onResponse(
                call: Call<AttendanceListResponse>,
                response: Response<AttendanceListResponse>
            ) {
                Log.d("pap list", response.body().toString())
                if (response.body()?.success == true){
                    try {
                        val data = response.body()?.data
                        dataAbsen = response.body()?.data!!
                        attendanceAdapter = AttendanceAdapter(this@AdminActivity, data)
                        listAbsen.layoutManager = LinearLayoutManager(
                            this@AdminActivity,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                        listAbsen.adapter = attendanceAdapter

                        val ok = data?.count() { it.status_masuk == "ONTIME" }
                        val nok = data?.count() { it.status_masuk == "TERLAMBAT" }
                        countOk.text = "On Time $ok Karyawan"
                        countNok.text = "Terlambat $nok Karyawan"
                    }catch (e:Exception){
                        Log.d("pap adapter",e.toString())
                    }

                }
            }

            override fun onFailure(call: Call<AttendanceListResponse>, t: Throwable) {
                Log.d("pap gagal", t.toString())
                Toast.makeText(this@AdminActivity, "Gagal load data", Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onClick(p0: View) {
        when(p0.id) {
            R.id.admin_btnPrint -> {
                Toast.makeText(this, "Export data absensi, harap tunggu...", Toast.LENGTH_SHORT).show()
                val hssfWorkbook = HSSFWorkbook()
                val hssfSheet = hssfWorkbook.createSheet("Custom Sheet")
                //---------------
                //---------------
                val hssfRowMainTitle = hssfSheet.createRow(0)
                hssfRowMainTitle.createCell(0).setCellValue("Absensi $date")
                //-------------
                //-------------
                val hssfRowTitle = hssfSheet.createRow(1)
                hssfRowTitle.createCell(0).setCellValue("no")
                hssfRowTitle.createCell(1).setCellValue("nama")
                hssfRowTitle.createCell(2).setCellValue("divisi")
                hssfRowTitle.createCell(3).setCellValue("jam")
                hssfRowTitle.createCell(4).setCellValue("lokasi")
                hssfRowTitle.createCell(5).setCellValue("status")
                //--------------
                //--------------
                var row = 2
                var srNo = 1
                for (a in dataAbsen) {
                    val hssfRow = hssfSheet.createRow(row)
                    val status = if (a.status_masuk.equals("OK")) "On Time" else "Terlambat"
                    hssfRow.createCell(0).setCellValue(srNo.toDouble())
                    hssfRow.createCell(1).setCellValue(a.user_detail.name)
                    hssfRow.createCell(2).setCellValue(a.user_detail.division)
                    hssfRow.createCell(3).setCellValue(a.jam_masuk)
                    hssfRow.createCell(4).setCellValue(a.lokasi_masuk.toString())
                    hssfRow.createCell(5).setCellValue(status)
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
                    Log.d("pap gagal print", e.toString())
                    e.printStackTrace()
                }
            }
        }
    }
}