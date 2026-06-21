package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.OkHttpClient

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val client = OkHttpClient()
        val BaseURL = "https://apis.data.go.kr/B551457/run/v2/"
        val API = "Your API"
        val code = "codes2" //코드정보
        val ttrp = "travelerTrainRunPlan2" //여객열차 운행계획
        val ttri = "travelerTrainRunInfo2" //여객열차 운행정보

        val autoCompTV = findViewById<AutoCompleteTextView>(R.id.autoCompleteStation)
        val gyeongbu = arrayOf("서울", "용산", "영등포", "안양", "수원", "오산",
            "서정리", "평택", "성환", "천안", "전의", "조치원", "부강", "신탄진",
            "대전", "옥천", "이원", "지탄", "심천", "각계", "영동", "황간", "추풍령",
            "김천", "구미", "약목", "왜관", "신동", "서대구", "대구", "동대구", "경산",
            "남성현", "청도", "상동", "밀양", "삼랑진", "원동", "물금", "화명", "구포",
            "사상", "부산",/*경부고속선*/"경주", "울산")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, gyeongbu)
        autoCompTV.setAdapter(adapter)

        val searchBtn = findViewById<Button>(R.id.button)
        val mapBtn = findViewById<Button>(R.id.button2)
        val favBtn = findViewById<Button>(R.id.button3)

        searchBtn.setOnClickListener {
            val station = autoCompTV.text.toString()
            val intent = Intent(this, MainActivity2::class.java)
            intent.putExtra("station", station)
            startActivity(intent)
            autoCompTV.text.clear()
        }

        mapBtn.setOnClickListener {
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
            autoCompTV.text.clear()
        }

        favBtn.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            intent.putExtra("station", "favStation")
            startActivity(intent)
            autoCompTV.text.clear()
        }
    }
}
