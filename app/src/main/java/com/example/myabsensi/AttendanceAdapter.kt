package com.example.myabsensi

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myabsensi.pojo.Attendance
import java.text.SimpleDateFormat

class AttendanceAdapter(private val mContext: Context?, private val data:List<Attendance>?): RecyclerView.Adapter<AttendanceAdapter.AttendanceHolder>() {
    lateinit private var attendanceHolder : AttendanceHolder
    lateinit private var layoutView : View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceHolder {
        layoutView = LayoutInflater.from(mContext).inflate(R.layout.item_absensi, parent, false)
        attendanceHolder = AttendanceHolder(layoutView)

        return attendanceHolder
    }

    override fun onBindViewHolder(holder: AttendanceHolder, position: Int) {
        val name = data?.get(position)?.user_detail?.name
        val time = data?.get(position)?.jam_masuk
        val division = data?.get(position)?.user_detail?.division
        val status = data?.get(position)?.status_masuk


        holder.timeTv.text = time
        holder.nameTv.text = name
        holder.divisionTv.text = division
        if (status.equals("ONTIME")){
            holder.statusIv.setImageResource(R.drawable.ic_ok_new)
        }else{
            holder.statusIv.setImageResource(R.drawable.ic_nok)
        }
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    inner class AttendanceHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var timeTv : TextView
        var nameTv : TextView
        var divisionTv : TextView
        var statusIv : ImageView

        init {
            timeTv = itemView.findViewById(R.id.absensi_time)
            nameTv = itemView.findViewById(R.id.absensi_name)
            divisionTv = itemView.findViewById(R.id.absensi_division)
            statusIv = itemView.findViewById(R.id.absensi_status)
        }
    }
}