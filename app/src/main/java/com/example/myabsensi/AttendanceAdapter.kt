package com.example.myabsensi

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myabsensi.pojo.Absensi
import com.example.myabsensi.utils.Helper

class AttendanceAdapter(private val mContext: Context?, private var data:List<Absensi>?): RecyclerView.Adapter<AttendanceAdapter.AttendanceHolder>() {
    lateinit private var attendanceHolder : AttendanceHolder
    lateinit private var layoutView : View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceHolder {
        layoutView = LayoutInflater.from(mContext).inflate(R.layout.item_absensi, parent, false)
        attendanceHolder = AttendanceHolder(layoutView)

        return attendanceHolder
    }

    override fun onBindViewHolder(holder: AttendanceHolder, position: Int) {
        val name = data?.get(position)?.user?.name
        val division = data?.get(position)?.user?.divisi
        val status = data?.get(position)?.status_masuk
        val statusPulang = data?.get(position)?.status_pulang
        val date = Helper.Utils.indonesianDate(data?.get(position)!!.created_at)
        if (position > 0) {
            val dateTemp = Helper.Utils.indonesianDate(data!![position-1].created_at)
            if (date == dateTemp) {
                holder.absensiDate.visibility = View.GONE
            } else {
                holder.absensiDate.visibility = View.VISIBLE
            }
        } else {
            holder.absensiDate.visibility = View.VISIBLE
        }

        var keterangan = ""
        if (data!![position].jam_pulang == null) {
            keterangan = "Tidak Absen"

        }else{
            if (statusPulang == "late"){
                keterangan = "Pulang Awal"
            }else{
                keterangan = "Ok"
            }
        }
        holder.absensiDate.text = date
        holder.absensiName.text = name
        holder.absnesiDivision.text = "Divisi : $division"
        holder.absensiMasukTime.text = data!![position].jam_masuk
        holder.absensiMasukLoc.text = if (data!![position].status_masuk.equals("late")) "Terlambat" else "Ok"
        holder.absensiPulangTime.text = data!![position].jam_pulang
        holder.absensiPulangLoc.text = keterangan

        if (status.equals("late") || statusPulang.equals("late") || statusPulang.isNullOrEmpty()){
            holder.absensiStatus.setImageResource(R.drawable.ic_nok)
        }else{
            holder.absensiStatus.setImageResource(R.drawable.ic_ok_new)
        }
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    fun update(result: List<Absensi>?) {
        this.data = result
        notifyDataSetChanged()
    }

    inner class AttendanceHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var absensiDate : TextView
        var absensiName : TextView
        var absnesiDivision : TextView
        var absensiStatus : ImageView
        var absensiMasukTime : TextView
        var absensiMasukLoc : TextView
        var absensiPulangTime : TextView
        var absensiPulangLoc : TextView

        init {
            absensiDate = itemView.findViewById(R.id.absensi_date)
            absensiName = itemView.findViewById(R.id.absensi_name)
            absnesiDivision = itemView.findViewById(R.id.absensi_division)
            absensiStatus = itemView.findViewById(R.id.absensi_status)
            absensiMasukTime = itemView.findViewById(R.id.absensi_masukTime)
            absensiMasukLoc = itemView.findViewById(R.id.absensi_masukLoc)
            absensiPulangTime = itemView.findViewById(R.id.absensi_pulangTime)
            absensiPulangLoc = itemView.findViewById(R.id.absensi_pulangLoc)
        }
    }
}