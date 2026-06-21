package com.example.myapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.OkHttpClient
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.net.URLEncoder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.google.gson.Gson
import java.io.IOException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.widget.LinearLayout
import android.widget.TextView
import android.content.Context
import android.widget.Button
import android.widget.Toast

class MainActivity2 : AppCompatActivity() {
    var suwonList = ArrayList<ApiResponse.TrainItem>()
    var destList = ArrayList<ApiResponse.TrainItem>()
    var planList = ArrayList<PlanResponse.PlanItem>()
    var requestCount = 0
    var station: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val client = OkHttpClient()
        val BaseURL = "https://apis.data.go.kr/B551457/run/v2/"
        val API = "Your API"
        val ttrp = "travelerTrainRunPlan2"
        val ttri = "travelerTrainRunInfo2"

        station = intent.getStringExtra("station")
        if(station == "favStation") {
            val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            station = sharedPref.getString("fav_station", "서울")
        }

        val favBtn = findViewById<Button>(R.id.button4)
        favBtn.setOnClickListener {
            station?.let { currentStation ->
                val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("fav_station", currentStation)
                    apply()
                }
            }
            Toast.makeText(this, "즐겨찾기 등록완료", Toast.LENGTH_SHORT).show()
        }

        val gyeongbu = arrayOf("서울", "용산", "영등포", "안양", /*"수원",*/ "오산",
            "서정리", "평택", "성환", "천안", "전의", "조치원", "부강", "신탄진",
            "대전", "옥천", "이원", "지탄", "심천", "각계", "영동", "황간", "추풍령",
            "김천", "구미", "약목", "왜관", "신동", "서대구", "대구", "동대구", "경산",
            "남성현", "청도", "상동", "밀양", "삼랑진", "원동", "물금", "화명", "구포",
            "사상", "부산",/*경부고속선*/"경주", "울산")
        if(station == "수원"){
            Toast.makeText(this, "현재 수원역에 있습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
        if (station !in gyeongbu){
            Toast.makeText(this, "존재하지 않는 역입니다.", Toast.LENGTH_SHORT).show()
            finish()
        }

        val stn = URLEncoder.encode(station, "UTF-8")
        val today = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val yesterday = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"))

        val urlSuwon = "${BaseURL}${ttri}?serviceKey=${API}&numOfRows=1000&cond%5Brun_ymd%3A%3AGTE%5D=${yesterday}&cond%5Brun_ymd%3A%3ALTE%5D=${yesterday}&cond%5Bstn_nm%3A%3AEQ%5D=%EC%88%98%EC%9B%90"
        val urlDest = "${BaseURL}${ttri}?serviceKey=${API}&numOfRows=1000&cond%5Brun_ymd%3A%3AGTE%5D=${yesterday}&cond%5Brun_ymd%3A%3ALTE%5D=${yesterday}&cond%5Bstn_nm%3A%3AEQ%5D=${stn}"
        val urlPlan = "${BaseURL}${ttrp}?serviceKey=${API}&numOfRows=3000&cond%5Brun_ymd%3A%3AGTE%5D=${today}&cond%5Brun_ymd%3A%3ALTE%5D=${today}"

        val req1 = Request.Builder().url(urlSuwon).build()
        client.newCall(req1).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { e.printStackTrace() }
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    suwonList = Gson().fromJson(response.body!!.string(), ApiResponse::class.java).response.body.items.item
                    requestCount++
                    if (requestCount == 3) compareLists()
                }
            }
        })

        val req2 = Request.Builder().url(urlDest).build()
        client.newCall(req2).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { e.printStackTrace() }
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    destList = Gson().fromJson(response.body!!.string(), ApiResponse::class.java).response.body.items.item
                    requestCount++
                    if (requestCount == 3) compareLists()
                }
            }
        })

        val req3 = Request.Builder().url(urlPlan).build()
        client.newCall(req3).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { e.printStackTrace() }
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    planList = Gson().fromJson(
                        response.body!!.string(),
                        PlanResponse::class.java
                    ).response.body.items.item
                    requestCount++
                    if (requestCount == 3) compareLists()
                }
            }
        })
    }

    fun compareLists() {
        val pairedTrains = ArrayList<TrainPair>()

        suwonList.forEach { suwon ->
            val dest = destList.find { it.trn_no == suwon.trn_no }
            val plan = planList.find { it.trn_no == suwon.trn_no }

            if (dest != null && plan != null) {
                if (suwon.trn_arvl_dt != null && dest.trn_arvl_dt != null
                    && suwon.trn_arvl_dt < dest.trn_arvl_dt) {
                    pairedTrains.add(
                        TrainPair(
                            trn_no = suwon.trn_no,
                            suwon_arvl_dt = suwon.trn_arvl_dt,
                            dest_arvl_dt = dest.trn_arvl_dt,
                            uppln_dn_se_cd = suwon.uppln_dn_se_cd,
                            dptre_stn_nm = plan.dptre_stn_nm,
                            arvl_stn_nm = plan.arvl_stn_nm
                        )
                    )
                }
            }
        }
        pairedTrains.sortBy { it.suwon_arvl_dt }
        CoroutineScope(Dispatchers.Main).launch {
            val container = findViewById<LinearLayout>(R.id.container)
            container.removeAllViews()

            for (train in pairedTrains) {
                val itemView = layoutInflater.inflate(R.layout.trainplan, container, false)

                itemView.findViewById<TextView>(R.id.tv_trn_no).text = train.trn_no
                itemView.findViewById<TextView>(R.id.tv_route).text = "${train.dptre_stn_nm} → ${train.arvl_stn_nm}"
                itemView.findViewById<TextView>(R.id.tv_time).text = "수원 ${train.suwon_arvl_dt?.substring(11, 16)} → ${station} ${train.dest_arvl_dt?.substring(11, 16)}"

                container.addView(itemView)
            }
        }
    }
}

data class TrainPair(
    val trn_no: String?,
    val suwon_arvl_dt: String?,
    val dest_arvl_dt: String?,
    val uppln_dn_se_cd: String?,
    val dptre_stn_nm: String?,
    val arvl_stn_nm: String?
)

data class ApiResponse(val response: TrainResponse) {
    data class TrainResponse(val body: Body)
    data class Body(val items: Items)
    data class Items(val item: ArrayList<TrainItem>)

    data class TrainItem(
        val stn_nm: String? = null,
        val trn_no: String? = null,
        val trn_arvl_dt: String? = null,
        val uppln_dn_se_cd: String? = null
    )
}

data class PlanResponse(val response: PlanTrainResponse) {
    data class PlanTrainResponse(val body: PlanBody)
    data class PlanBody(val items: PlanItems)
    data class PlanItems(val item: ArrayList<PlanItem>)

    data class PlanItem(
        val arvl_stn_nm: String? = null,
        val dptre_stn_nm: String? = null,
        val trn_no: String? = null,
        val trn_plan_arvl_dt: String? = null,
        val trn_plan_dptre_dt: String? = null
    )
}