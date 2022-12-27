package com.example.myabsensi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myabsensi.pojo.UserHistoryAbsentResponse
import com.example.myabsensi.retrofit.ApiService
import com.example.myabsensi.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryFragment : Fragment() {
    private lateinit var prefManager: PrefManager

    private var user_id = 0
    private var token_user = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)

    }

    private fun init(){
        prefManager = PrefManager(requireActivity().applicationContext)

        token_user = prefManager.getToken().toString()
        user_id = prefManager.getId()

        println("pap history activity $token_user $user_id")
        getHistoryAbsent()
    }

    private fun getHistoryAbsent(){
        val auth = "Bearer $token_user"
        ApiService.endpoint.getHistoryAbsent(user_id, auth).enqueue(object :
            Callback<UserHistoryAbsentResponse> {
            override fun onResponse(
                call: Call<UserHistoryAbsentResponse>,
                response: Response<UserHistoryAbsentResponse>
            ) {
                println("pap riwayat absensi ${response.body()?.data}")
            }

            override fun onFailure(call: Call<UserHistoryAbsentResponse>, t: Throwable) {
                println("pap gagal get history absent ${t.toString()}")
            }

        })
    }

    override fun onStart() {
        super.onStart()
        init()
    }

}