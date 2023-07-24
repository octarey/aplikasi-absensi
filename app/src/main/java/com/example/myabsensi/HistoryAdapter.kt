package com.example.myabsensi

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myabsensi.pojo.Absensi
import com.example.myabsensi.pojo.Attendance
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(private val mContext: Context?, private val data:List<Absensi>?): RecyclerView.Adapter<HistoryAdapter.HistoryHolder>() {
    lateinit private var attendanceHolder : HistoryHolder
    lateinit private var layoutView : View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHolder {
        layoutView = LayoutInflater.from(mContext).inflate(R.layout.item_absensi_user, parent, false)
        attendanceHolder = HistoryHolder(layoutView)

        return attendanceHolder
    }

    override fun onBindViewHolder(holder: HistoryHolder, position: Int) {
        val statusMasuk = data?.get(position)?.status_masuk
        val statusPulang = data?.get(position)?.status_pulang
        val date = data?.get(position)?.created_at
        val dateString = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id")).format(date).toString()

        holder.dateLabel_tv.text = dateString
        holder.jamMasuk_tv.text = if (data?.get(position)?.jam_masuk == null) "-" else data?.get(position)?.jam_masuk
        holder.jamPulang_tv.text = if (data?.get(position)?.jam_pulang == null) "-" else data?.get(position)?.jam_pulang

        holder.lokasiMasuk_tv.text = if (data?.get(position)?.jam_masuk == null) "-" else "di kantor"
        holder.lokasiPulang_tv.text = if (data?.get(position)?.jam_pulang == null) "-" else "di kantor"



        if (statusMasuk.equals("late") || statusPulang.equals("late")){
            holder.historyStatus_tv.visibility = View.VISIBLE
        }else{
            holder.historyStatus_tv.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    inner class HistoryHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var dateLabel_tv: TextView
        var jamMasuk_tv : TextView
        var lokasiMasuk_tv : TextView
        var jamPulang_tv: TextView
        var lokasiPulang_tv: TextView
        var historyStatus_tv: TextView

        init {
            dateLabel_tv = itemView.findViewById(R.id.history_date)
            jamMasuk_tv = itemView.findViewById(R.id.history_masukTime)
            lokasiMasuk_tv = itemView.findViewById(R.id.history_masukLoc)
            jamPulang_tv = itemView.findViewById(R.id.history_pulangTime)
            lokasiPulang_tv = itemView.findViewById(R.id.history_pulangLoc)
            historyStatus_tv = itemView.findViewById(R.id.history_status)
        }
    }
}