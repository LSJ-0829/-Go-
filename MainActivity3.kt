package com.example.myapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.content.Intent
import android.view.LayoutInflater

class MainActivity3 : AppCompatActivity() {
    val gyeongbu = arrayOf("서울", "용산", "영등포", "안양", "수원", "오산",
        "서정리", "평택", "성환", "천안", "전의", "조치원", "부강", "신탄진",
        "대전", "옥천", "이원", "지탄", "심천", "각계", "영동", "황간", "추풍령",
        "김천", "구미", "약목", "왜관", "신동", "서대구", "대구", "동대구", "경산",
        "남성현", "청도", "상동", "밀양", "삼랑진", "원동", "물금", "화명", "구포",
        "사상", "부산") //경부본선
    val gyeongbuExpress = arrayOf("서울", "영등포", "수원", "대전", "동대구", "경주", "울산", "부산") //경부고속선

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main3)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var slowBtn = findViewById<Button>(R.id.btnslow)
        var expressBtn = findViewById<Button>(R.id.btnexpress)

        showStation(gyeongbu)

        slowBtn.setOnClickListener {
            showStation(gyeongbu)
        }

        expressBtn.setOnClickListener {
            showStation(gyeongbuExpress)
        }
    }

    fun showStation(stations: Array<String>) {
        val container = findViewById<LinearLayout>(R.id.container2)
        container.removeAllViews()

        stations.forEachIndexed { index, stationName ->
            val itemView = LayoutInflater.from(this).inflate(R.layout.station, container, false)

            val tvStation = itemView.findViewById<TextView>(R.id.tv_station_name)
            tvStation.text = stationName

            itemView.setOnClickListener {
                val intent = Intent(this, MainActivity2::class.java)
                intent.putExtra("station", stationName)
                startActivity(intent)
            }

            container.addView(itemView)
        }
    }
}