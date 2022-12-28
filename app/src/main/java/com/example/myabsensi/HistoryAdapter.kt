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
        val status = data?.get(position)?.status_masuk
        val date = data?.get(position)?.tanggal
        val dateString = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id")).format(date).toString()
        val jarakMasuk = data?.get(position)?.lokasi_masuk.toString()
        val jarakPulang = data?.get(position)?.lokasi_pulang.toString()

        holder.dateLabel_tv.text = dateString
        holder.jamMasuk_tv.text = data?.get(position)?.jam_masuk
        holder.lokasiPulang_tv.text = if (jarakMasuk.equals("0")) "di kantor" else "$jarakMasuk Km dari kantor"
        holder.jamPulang_tv.text = data?.get(position)?.jam_pulang
        holder.lokasiPulang_tv.text = if (jarakPulang.equals("0")) "di kantor" else "$jarakPulang Km dari kantor"

        if (status.equals("ONTIME")){
            holder.historyStatus_tv.visibility = View.GONE
        }else{
            holder.historyStatus_tv.visibility = View.VISIBLE
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