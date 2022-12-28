package com.example.myabsensi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.viewpager2.widget.ViewPager2
import com.example.myabsensi.pojo.UserHistoryAbsentResponse
import com.example.myabsensi.retrofit.ApiService
import com.example.myabsensi.utils.PrefManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryActivity : AppCompatActivity() {
    val menus = arrayOf("Absensi", "Lembur", "Agenda")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        supportActionBar?.hide()

        val viewPager = findViewById<ViewPager2>(R.id.history_contentBox)
        val tabLayout = findViewById<TabLayout>(R.id.history_menuBox)
        val backBtn = findViewById<ImageButton>(R.id.history_backBtn)

        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = menus[position]
        }.attach()

        backBtn.setOnClickListener {
            onBackPressed()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}