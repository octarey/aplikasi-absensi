package com.example.myabsensi

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myabsensi.pojo.User

class UserAdapter(private val mContext: Context?): RecyclerView.Adapter<UserAdapter.UserHolder>() {
    lateinit private var userHolder : UserHolder
    lateinit private var layoutView : View
    private var onClickListener: OnClickListener? = null

    private var data  = ArrayList<User>()

    fun setData(data: List<User>){
        this.data = data as ArrayList<User> /* = java.util.ArrayList<gms.gabsen.tulungagung.data.local.Profile> */
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        layoutView = LayoutInflater.from(mContext).inflate(R.layout.item_user, parent, false)
        userHolder = UserHolder(layoutView)

        return userHolder
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        holder.namaTxt.text = data[position].name
        holder.divisiTxt.text = data[position].divisi

        holder.editBtn.setOnClickListener {
            onClickListener!!.edit(position, data[position] )
        }

        holder.deletBtn.setOnClickListener {
            onClickListener!!.delete(position, data[position] )
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class UserHolder(itemView: View): RecyclerView.ViewHolder(itemView){
      var namaTxt : TextView
      var divisiTxt : TextView
      var editBtn : Button
      var deletBtn : Button

        init {
            namaTxt = itemView.findViewById(R.id.user_nama)
            divisiTxt = itemView.findViewById(R.id.user_divisi)
            editBtn = itemView.findViewById(R.id.btn_edit)
            deletBtn = itemView.findViewById(R.id.btn_delete)
        }
    }

    // A function to bind the onclickListener.
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    // onClickListener Interface
    interface OnClickListener {
        fun edit(position: Int, data: User)
        fun delete(position: Int, data: User)
    }

}